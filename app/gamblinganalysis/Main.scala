package gamblinganalysis

import gamblinganalysis.analysis.OddsOptimiser
import gamblinganalysis.retriever.GameRetriever
import gamblinganalysis.retriever.odds.{OddsCheckerRetriever, SkybetRetriever}
import gamblinganalysis.util.exceptions.ParseException
import play.api.Logger

/**
  * Created by misha on 20/02/16.
  */
object Main {
  private val log = Logger(getClass)

  def main(args: Array[String]) {
    runGameRetriever()
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
}
