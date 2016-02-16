package gamblinganalysis.analysis

import gamblinganalysis.OddsCollection
import gamblinganalysis.odds.{OddsCollection, Odd}

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

/**
  * Created by misha on 10/02/16.
  */
object OddsOptimiser {
  def optimise(odds: Seq[OddsCollection]): OddsCollection = {
    val oddGroups = mutable.HashMap[String, ListBuffer[Odd]]()

    odds.foreach(o => {
      o.odds.foreach(o1 => {
        if (oddGroups.contains(o1.title)) {
          oddGroups(o1.title).+=(o1)
        } else {
          oddGroups(o1.title) = ListBuffer(o1)
        }
      })
    })

    val sorted = oddGroups.map({ case (outcome, odds) =>
      (outcome, odds.sortBy(_.getProbability).head)
    })

    val completeOdds = sorted.map({ case (_, odd) => odd}).toSeq

    new OddsCollection(completeOdds)
  }
}
