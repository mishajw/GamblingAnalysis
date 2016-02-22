package gamblinganalysis.accounts

import gamblinganalysis.Bookie
import gamblinganalysis.analysis.OddsOptimiser
import gamblinganalysis.odds.{Odd, OddsCollection}
import gamblinganalysis.plans.{FullPlan, LinkedOdd, LinkedPlan}

/**
  * Created by misha on 16/02/16.
  */
class AccountsCollection(val accounts: Seq[Account]) {

  def mostProfitable(odds: OddsCollection): Option[FullPlan] = {
    val sortedOdds: Seq[Seq[Odd]] =
      (OddsOptimiser.getSortedOdds(odds) map { case (_, os) => os }).toSeq

    val allPossible = getAllPossibleOdds(sortedOdds)
    val allPlans = allPossible.flatMap(profitsOfCollection)
    val fullPlans = allPlans.map(_.asFullPlan)

    if (allPlans.isEmpty)
      None
    else
      Some(fullPlans.maxBy(_.roi))
  }

  private def getAllPossibleOdds(oddHeap: Seq[Seq[Odd]]): Seq[OddsCollection] = {
    if (oddHeap.size == 1) {
      oddHeap.head.map(o => new OddsCollection(Seq(o)))
    } else {
      val head = oddHeap.head
      val tail = oddHeap.slice(1, oddHeap.size)

      val processedTail = getAllPossibleOdds(tail)

      head flatMap { o =>
        processedTail map { oc =>
          new OddsCollection(o +: oc.odds)
        }
      }
    }
  }

  private def profitsOfCollection (oddsCollection: OddsCollection): Option[LinkedPlan] = {
    val paired = oddsCollection.odds.map(o => {
      getBestAccountForBookie(o.bookie) match {
        case Some(a) => Some(a, o)
        case None => None
      }
    })

    if (paired.length != paired.flatten.length)
      None
    else
      Some(new LinkedPlan(paired.flatten map { case (acc, odd) =>
        LinkedOdd(odd, acc)
      }))
  }

  private def getBestAccountForBookie(bookie: Bookie): Option[Account] = {
    accounts.filter(_.bookie == bookie) match {
      case Nil => None
      case as => Some(as.maxBy(_.amount))
    }
  }
}
