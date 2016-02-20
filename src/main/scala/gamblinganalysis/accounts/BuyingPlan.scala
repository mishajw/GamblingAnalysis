package gamblinganalysis.accounts

import gamblinganalysis.odds.Odd

/**
  * Created by misha on 16/02/16.
  */
class BuyingPlan(val whichAccounts: Seq[(Account, Odd)]) {
  def profit: BigDecimal = ???
}
