package gamblinganalysis

import gamblinganalysis.analysis.OddsOptimiser
import gamblinganalysis.retriever.GameRetriever
import gamblinganalysis.retriever.odds.OddsCheckerRetriever
import gamblinganalysis.util.exceptions.ParseException

/**
  * Created by misha on 10/02/16.
  */
object Main {
  def main(args: Array[String]): Unit = {
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
      .sortBy { case (odds, ir) => ir }
      .map { case (odds, ir) => odds }
      .foreach(o => {
        o.printSafeBet()
        println()
      })


//    val odds = OddsRetriever.getOdds("http://www.oddschecker.com/tennis/atp-marseille/peter-gojowczyk-v-kenny-de-schepper/winner")
//    val optimum = OddsOptimiser.optimise(odds)
//
//    optimum.betSafely()

//    println(SkybetRetriever.run().mkString("\n"))

  }
}
