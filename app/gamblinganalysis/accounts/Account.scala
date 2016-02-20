package gamblinganalysis.accounts

import gamblinganalysis.{AccountOwner, Bookie}

/**
  * Created by misha on 16/02/16.
  */
class Account(val name: AccountOwner, var amount: BigDecimal, val bookie: Bookie) {
  override def toString: String = s"Account($name, $amount, $bookie)"
}
