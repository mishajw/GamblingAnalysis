package gamblinganalysis

import gamblinganalysis.retriever.{GameRetriever, OddsRetriever}

/**
  * Created by misha on 10/02/16.
  */
object Main {
  def main(args: Array[String]): Unit = {
    GameRetriever.retrieve.foreach(g => {
      val odds = OddsRetriever.getOdds(g)
      val optimum = OddsOptimiser.optimise(odds)

      optimum.betSafely()
      println("\n\n")
    })
  }
}
