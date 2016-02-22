package gamblinganalysis

import gamblinganalysis.accounts.{Account, BuyingPlan}
import gamblinganalysis.analysis.{AggressiveSimulator, OddsOptimiser}
import gamblinganalysis.factory.{BookieFactory, GameFactory, GameOutcomeFactory, UserFactory}
import gamblinganalysis.odds.Odd
import gamblinganalysis.retriever.GameRetriever
import gamblinganalysis.retriever.odds.{OddsCheckerRetriever, SkybetRetriever}
import gamblinganalysis.util.db.{GeneralDBHandler, UserDBHandler}
import gamblinganalysis.util.exceptions.ParseException
import play.api.Logger

/**
  * Created by misha on 20/02/16.
  */
object Main {
  private val log = Logger(getClass)

  def main(args: Array[String]) {
    GeneralDBHandler.reset()
    println(UserDBHandler.users.mkString("\n"))
  }

  def runOddsChecker() = {
    log.info("Starting OddsChecker")

    val odds = OddsCheckerRetriever.getOdds("http://www.oddschecker.com/tennis/atp-marseille/peter-gojowczyk-v-kenny-de-schepper/winner")
    val optimum = OddsOptimiser.optimise(odds)

    optimum.printSafeBet()
  }

  def runGameRetriever() = {
    log.info("Starting GameRetriever")

    GameRetriever.retrieve.flatMap(g => {
      try {
        val odds = OddsCheckerRetriever.getOdds(g)
        val optimum = OddsOptimiser.optimise(odds)
        Some(optimum, optimum.getInvestmentReturn)
      } catch {
        case e: Exception => None
        case e: ParseException => None
      }
    })
      .sortBy { case (odds, ir) => -ir }
      .map { case (odds, ir) => odds }
      .foreach(o => {
        o.printSafeBet()
        println()
      })
  }

  def runSkybet() = {
    log.info("Starting Skybet")

    val results = SkybetRetriever.run()
    results.foreach(r => log.info(r.toString))
  }

  def runBuyingPlan() = {
    log.info("Starting buying plan")

    val game = GameFactory get Set("Win", "Draw", "Lose")
    val bet365: Bookie = BookieFactory get "bet365"

    val plan = new BuyingPlan(Seq(
      (
        new Account(UserFactory get "Misha", BigDecimal(7), bet365),
        new Odd(3, 1, GameOutcomeFactory get ("Win", game), bet365)
      ),
      (
        new Account(UserFactory get "Hannah", BigDecimal(10), bet365),
        new Odd(2, 1, GameOutcomeFactory get ("Draw", game), bet365)
      ),
      (
        new Account(UserFactory get "Jodie", BigDecimal(10), bet365),
        new Odd(2, 1, GameOutcomeFactory get ("Lose", game), bet365)
      )
    ))

    log.info(s"Profit: ${plan.profit}, Limiting account: ${plan.getLimitingAccount}")
  }

  def runAggressiveSimulator() = {
    log.info("Starting accounts collection")

    AggressiveSimulator.run()
  }
}
