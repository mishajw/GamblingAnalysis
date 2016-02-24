package gamblinganalysis.util.db

import gamblinganalysis.{Sport, Game}
import play.api.Logger
import scalikejdbc._

object GameDetailsDBHandler extends BaseDBHandler {
  private val log = Logger(getClass)

  def insertOdd() = {

  }

  def insertGame(game: Game) = {
    val sportId = insertSport(game.sport)

    val gameId =
      sql"""
           INSERT INTO game(sport_id) VALUES ($sportId)
         """.update.apply()

    log.info(s"Inserted game with ID: $gameId")

    game.outcomes.foreach { o =>
      sql"""
           INSERT INTO game_outcome(game_id, outcome) VALUES ($gameId, $o)
         """.update.apply()
    }
  }

  def insertSport(sport: Sport) = {
    val optId =
      sql"""
           SELECT id FROM sport WHERE title = ${sport.title}
         """.map(r => r.int("id")).single.apply()

    optId match {
      case Some(id) => id
      case x =>
        sql"""
             INSERT INTO sport(title) VALUES (${sport.title})
           """.update.apply()
    }
  }
}
