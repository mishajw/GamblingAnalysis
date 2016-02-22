package gamblinganalysis.analysis

import gamblinganalysis.GameOutcome
import gamblinganalysis.odds.{Odd, OddsCollection}
import gamblinganalysis.plans.{ValuedPlan, ValuedOdd}

/**
  * Created by misha on 10/02/16.
  */
object OddsOptimiser {

  private val defaultBetAmount = 100

  /**
    * Get the best odds combination
    *
    * @param odds odds to choose from
    * @return odds collection of best odds
    */
  def optimise(odds: OddsCollection): ValuedPlan = {
    val topOdds = getSortedOdds(odds) map { case (_, o :: os) => o }
    planFromOdds(topOdds.toSeq)
  }

  /**
    * Create an optimum buying plan from odds
    *
    * @param odds odds to use
    * @return the plan (keeps coming up again)
    */
  def planFromOdds(odds: Seq[Odd]): ValuedPlan = {
    val totalProbabilities = odds.map(_.getProbability).sum

    val amounts = odds map { o =>
      ValuedOdd(o, (o.getProbability / totalProbabilities) * defaultBetAmount)
    }

    new ValuedPlan(amounts)
  }

  /**
    * Get odds sorted by probability
    *
    * @param odds odds to choose from
    * @return list of list of best odds
    */
  def getSortedOdds(odds: OddsCollection): Map[GameOutcome, Seq[Odd]] = {
    val oddGroups = odds.groupedOutcome()
    oddGroups map { case (outcome, os) =>
      outcome -> os.sortBy(-_.getProbability)
    }
  }
}
