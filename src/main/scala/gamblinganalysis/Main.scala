package gamblinganalysis

/**
  * Created by misha on 10/02/16.
  */
object Main {
  def main(args: Array[String]) = {
    val odds = OddsCheckerRetriever.getOdds
    println(odds.mkString("\n"))

    println()

    val optimum = OddsOptimiser.optimise(odds)
    println(s"\n$optimum => ${optimum.getAllProbabilities}")

    optimum.betSafely()
  }
}
