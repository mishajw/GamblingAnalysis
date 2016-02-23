package gamblinganalysis.accounts

import gamblinganalysis.{Bookie, User}

/**
  * Created by misha on 16/02/16.
  */
class Account(val name: User, var amount: BigDecimal, val bookie: Bookie) {
  override def toString: String = s"Account($name, $amount, $bookie)"


}
