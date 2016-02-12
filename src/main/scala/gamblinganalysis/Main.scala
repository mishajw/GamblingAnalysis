package gamblinganalysis

/**
  * Created by misha on 10/02/16.
  */
object Main {
  def main(args: Array[String]): Unit = {
    val odds = OddsRetriever.getOdds
    val optimum = OddsOptimiser.optimise(odds)

    optimum.betSafely()
  }
}
