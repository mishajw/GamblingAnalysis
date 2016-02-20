package gamblinganalysis.analysis

import gamblinganalysis.odds.{Odd, OddsCollection}

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

/**
  * Created by misha on 10/02/16.
  */
object OddsOptimiser {

  type OddHeap = Seq[Seq[Odd]]

  /**
    * Get the best odds combination
    * @param odds odds to choose from
    * @return odds collection of best odds
    */
  def optimise(odds: Seq[OddsCollection]): OddsCollection = {
    val topOdds = getSortedOdds(odds).map(_.head)
    new OddsCollection(topOdds)
  }

  /**
    * Get odds sorted by probability
    * @param odds odds to choose from
    * @return list of list of best odds
    */
  def getSortedOdds(odds: Seq[OddsCollection]): OddHeap = {
    val oddGroups = separateOdds(odds)
    oddGroups.map(_.sortBy(_.getProbability))
  }

  /**
    * Separate the odds into the different teams
    * @param odds odds to separate
    * @return list of list of odd groups
    */
  private def separateOdds(odds: Seq[OddsCollection]): OddHeap = {
    val oddGroups = new mutable.HashMap[String, ListBuffer[Odd]]()

    odds.foreach(o => {
      o.odds.foreach(o1 => {
        if (oddGroups.contains(o1.title)) {
          oddGroups(o1.title).+=(o1)
        } else {
          oddGroups(o1.title) = ListBuffer(o1)
        }
      })
    })

    oddGroups.values.toSeq
  }
}
