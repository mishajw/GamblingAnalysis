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

    val idOpt = sql"""
         SELECT G.id
         FROM game G
         WHERE (
            SELECT COUNT(1)
            FROM game_outcome O
            WHERE O.outcome IN (${game.outcomes})
            AND O.game_id = G.id
         ) = ${game.outcomes.size}
       """.map(_.int("id")).single.apply()

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
}
