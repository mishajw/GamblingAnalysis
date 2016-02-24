package gamblinganalysis.util.db

import gamblinganalysis.odds.Odd
import gamblinganalysis.{Bookie, Sport, Game}
import play.api.Logger
import scalikejdbc._

object GameDetailsDBHandler extends BaseDBHandler {
  private val log = Logger(getClass)

  def insertOdd(odd: Odd): Int = {
    val gameId = insertGame(odd.game)
    val bookieId = insertBookie(odd.bookie)

    val outcomeId =
      sql"""
           SELECT id FROM game_outcome
           WHERE outcome = ${odd.outcome}
           AND game_id = $gameId
         """.map(_.int("id")).single.apply().get

    sql"""
         INSERT INTO odd(numerator, denominator, time, bookie_id, game_id, outcome_id)
         VALUES (
            ${odd.gains},
            ${odd.base},
            0,
            $bookieId,
            $gameId,
            $outcomeId)

       """.updateAndReturnGeneratedKey().apply().toInt
  }

  def insertGame(game: Game): Int = {
    val sportId = insertSport(game.sport)

    val idOpt = getGameId(game)

    log.debug("ID opt for inserting game: " + idOpt)

    idOpt match {
      case Some(id) => id
      case None =>
        val gameId =
          sql"""
           INSERT INTO game(sport_id) VALUES ($sportId)
         """.updateAndReturnGeneratedKey().apply().toInt

        log.info(s"Inserted game with ID: $gameId")

        game.outcomes.foreach { o =>
          sql"""
           INSERT INTO game_outcome(game_id, outcome) VALUES ($gameId, $o)
         """.update.apply()
        }

        gameId
    }
  }

  def insertSport(sport: Sport): Int = {
    val optId =
      sql"""
           SELECT id FROM sport WHERE title = ${sport.title}
         """.map(_.int("id")).single.apply()

    optId match {
      case Some(id) => id
      case x =>
        sql"""
             INSERT INTO sport(title) VALUES (${sport.title})
           """.updateAndReturnGeneratedKey().apply().toInt
    }
  }

  def insertBookie(bookie: Bookie): Int = {
    val optId =
      sql"""
           SELECT id FROM bookie WHERE name = ${bookie.name}
         """.map(_.int("id")).single.apply()

    optId match {
      case Some(id) => id
      case x =>
        sql"""
             INSERT INTO bookie(name) VALUES (${bookie.name})
           """.updateAndReturnGeneratedKey().apply().toInt
    }
  }

  def allGames: Seq[Game] = {
    sql"""
         SELECT GROUP_CONCAT(O.outcome) AS outcomes, S.title AS sport
         FROM game G, game_outcome O, sport S
         WHERE G.id = O.game_id
         AND G.sport_id = S.id
         GROUP BY O.game_id
       """.map(r => Game(r.string("outcomes").split(",").toSet, Sport(r.string("sport"))))
            .list.apply()
  }

  def oddsForGame(game: Game): Seq[Odd] = {
    sql"""
         SELECT O.numerator, O.denominator, O.time, B.name AS bookie, OC.outcome AS outcome
         FROM (
            SELECT *
            FROM odd
            GROUP BY game_id, bookie_id, outcome_id
            ORDER BY odd.time DESC
         ) AS O, bookie B, game_outcome OC
         WHERE O.game_id = ${getGameId(game).get}
         AND O.bookie_id = B.id
         AND O.outcome_id = OC.id
       """.map(r => new Odd(r.int("numerator"), r.int("denominator"), r.string("outcome"), game, Bookie(r.string("bookie"))))
            .list.apply()
  }

  def getGameId(game: Game): Option[Int] = {
    sql"""
         SELECT G.id
         FROM game G
         WHERE (
            SELECT COUNT(1)
            FROM game_outcome O
            WHERE O.outcome IN (${game.outcomes})
            AND O.game_id = G.id
         ) = ${game.outcomes.size}
       """.map(_.int("id")).single.apply()
  }
}
