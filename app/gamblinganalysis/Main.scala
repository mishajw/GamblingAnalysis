package gamblinganalysis

import gamblinganalysis.accounts.Account
import gamblinganalysis.analysis.{AggressiveSimulator, BuyingPlan, OddsOptimiser}
import gamblinganalysis.factory._
import gamblinganalysis.odds.Odd
import gamblinganalysis.retriever.GameRetriever
import gamblinganalysis.retriever.odds.{actors, OddsCheckerRetriever, SkybetRetriever}
import gamblinganalysis.util.db.{UserDBHandler, GeneralDBHandler, GameDetailsDBHandler}
import gamblinganalysis.util.exceptions.ParseException
import play.api.Logger

/**
  * Created by misha on 20/02/16.
  */
object Main {
  private val log = Logger(getClass)

  def main(args: Array[String]): Unit = {
//    GeneralDBHandler.reset()
//
//    val sport = SportFactory get "Football"
//    val game = GameFactory get (Set("Win", "Lose", "Draw"), sport)
//    val bookie = BookieFactory get "Bet 365"
//
//    val odd1 = new Odd(3, 1, "Win", game, bookie)
//    val odd2 = new Odd(3, 1, "Lose", game, bookie)
//
//    GameDetailsDBHandler.insertOdd(odd1)
//    GameDetailsDBHandler.insertOdd(odd2)
//    actors.start()
//    GameDetailsDBHandler.allGames.foreach(g => {
//      println(OddsOptimiser.optimise(GameDetailsDBHandler.oddsForGame(g)))
//    })

    GeneralDBHandler.reset()
    val user = User("Misha")
    val bookie = Bookie("Bet 365")
    val account = new Account(user, BigDecimal(10), bookie)

    UserDBHandler.insertAccount(account)

    println(UserDBHandler.accounts.mkString("\n"))
  }

  def runOddsChecker() = {
    log.info("Starting OddsChecker")

    val odds = OddsCheckerRetriever.retrieve("http://www.oddschecker.com/tennis/atp-marseille/peter-gojowczyk-v-kenny-de-schepper/winner")
    val optimum = OddsOptimiser.optimise(odds)

    optimum
  }

  def runGameRetriever() = {
    log.info("Starting GameRetriever")

    GameRetriever.retrieve.flatMap(g => {
      try {
        val odds = OddsCheckerRetriever.retrieve(g)
        val optimum = OddsOptimiser.optimise(odds)
        Some(optimum, optimum.roi)
      } catch {
        case e: ParseException => None
      }
    })
      .sortBy { case (odds, ir) => -ir }
      .map { case (odds, ir) => odds }
      .foreach(o => {
        println(s"$o\n")
      })
  }

  def runSkybet() = {
    log.info("Starting Skybet")

    val results = SkybetRetriever.retrieve()
    log.info(results.toString)
  }

  def runBuyingPlan() = {
    log.info("Starting buying plan")

    val game = GameFactory get (Set("Win", "Draw", "Lose"), SportFactory get "test_sport")
    val bet365: Bookie = BookieFactory get "bet365"

    val plan = new BuyingPlan(Seq(
      (
        new Odd(3, 1, "Win", game, bet365),
        new Account(UserFactory get "Misha", BigDecimal(7), bet365)
      ),
      (
        new Odd(2, 1, "Draw", game, bet365),
        new Account(UserFactory get "Hannah", BigDecimal(10), bet365)
      ),
      (
        new Odd(2, 1, "Lose", game, bet365),
        new Account(UserFactory get "Jodie", BigDecimal(10), bet365)
      )
    ))
  }

  def runAggressiveSimulator() = {
    log.info("Starting accounts collection")

    AggressiveSimulator.run()
  }
}
