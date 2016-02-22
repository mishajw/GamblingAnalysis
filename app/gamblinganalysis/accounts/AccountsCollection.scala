package gamblinganalysis.accounts

import gamblinganalysis.Bookie
import gamblinganalysis.analysis.{BuyingPlan, OddsOptimiser}
import gamblinganalysis.odds.{Odd, OddsCollection}

/**
  * Created by misha on 16/02/16.
  */
class AccountsCollection(val accounts: Seq[Account]) {

  def mostProfitable(odds: OddsCollection): Option[BuyingPlan] = {
    val sortedOdds: Seq[Seq[Odd]] =
      (OddsOptimiser.getSortedOdds(odds) map { case (_, os) => os }).toSeq

    val allPossible = getAllPossibleOdds(sortedOdds)
    val allPlans = allPossible.flatMap(profitsOfCollection)

    if (allPlans.isEmpty)
      None
    else
      Some(allPlans.maxBy(_.roi))
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

  private def profitsOfCollection (oddsCollection: OddsCollection): Option[BuyingPlan] = {
    val paired = oddsCollection.odds.map(o => {
      getBestAccountForBookie(o.bookie) match {
        case Some(a) => Some(a, o)
        case None => None
      }
    })

    if (paired.length != paired.flatten.length)
      None
    else
      Some(new BuyingPlan(paired.flatten map { case (acc, odd) =>
        (odd, acc)
      }))
  }

  private def getBestAccountForBookie(bookie: Bookie): Option[Account] = {
    accounts.filter(_.bookie == bookie) match {
      case Nil => None
      case as => Some(as.maxBy(_.amount))
    }
  }
}
