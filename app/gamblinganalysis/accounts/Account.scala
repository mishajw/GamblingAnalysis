package gamblinganalysis.accounts

import gamblinganalysis.{AccountOwner, Bookie}

/**
  * Created by misha on 16/02/16.
  */
class Account(val name: AccountOwner, val amount: BigDecimal, val bookie: Bookie) {

}
