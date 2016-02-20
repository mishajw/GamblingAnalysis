package gamblinganalysis.accounts

import gamblinganalysis.analysis.OddsOptimiser
import gamblinganalysis.analysis.OddsOptimiser.OddHeap
import gamblinganalysis.odds.OddsCollection

/**
  * Created by misha on 16/02/16.
  */
class AccountsCollection(val accounts: Seq[Account]) {
  def chooseOdds(odds: Seq[OddsCollection]): BuyingPlan = {
    val sortedOdds: OddHeap = OddsOptimiser.getSortedOdds(odds)

    getAllPossibleOdds(sortedOdds)
      .map(profitsOfCollection)
      .sortBy(_.profit)
      .head
  }

  private def getAllPossibleOdds(oddHeap: OddHeap): Seq[OddsCollection] = {
    ???
  }

  private def profitsOfCollection (oddsCollection: OddsCollection): BuyingPlan = {
    ???
  }
}
