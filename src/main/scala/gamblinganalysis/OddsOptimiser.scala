package gamblinganalysis

import scala.collection.mutable
import scala.collection.mutable.{ListBuffer, ArrayBuffer}

/**
  * Created by misha on 10/02/16.
  */
object OddsOptimiser {
  def optimise(odds: Seq[GamblingOdds]): GamblingOdds = {
    val oddGroups = mutable.HashMap[String, ListBuffer[(String, Odd)]]()

    odds.foreach(o => {
      o.odds.foreach(o1 => {
        if (oddGroups.contains(o1.title)) {
          oddGroups(o1.title).+=((o.source, o1))
        } else {
          oddGroups(o1.title) = ListBuffer((o.source, o1))
        }
      })
    })

    val sorted = oddGroups.map({ case (outcome, odds) =>
      (outcome, odds.sortBy(_._2.getProbability).head)
    })

    val completeSource = sorted.map({ case (_, (source, _)) => source}).mkString(", ")
    val completeOdds = sorted.map({ case (_, (_, odd)) => odd}).toSeq

    new GamblingOdds(completeOdds, completeSource)
  }
}
