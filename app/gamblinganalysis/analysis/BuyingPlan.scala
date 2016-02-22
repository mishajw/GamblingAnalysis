package gamblinganalysis.analysis

import gamblinganalysis.accounts.Account
import gamblinganalysis.odds.Odd

class BuyingPlan(pairTuples: Seq[(Odd, Option[BigDecimal], Option[Account])]) {

  case class OddPair(odd: Odd, money: Option[BigDecimal], account: Option[Account])

  /**
    * Used so we can have multiple constructors that takes lists
    * (ffs scala)
    */
  private type OddMoney = Seq[(Odd, BigDecimal)]
  private type OddAccount = Seq[(Odd, Account)]

  val pairs: Seq[OddPair] = pairTuples map {
    case (odd, mon, acc) => OddPair(odd, mon, acc)
  }

  def this(pairTuples: OddMoney) {
    this(pairTuples.map(t => (t._1, Some(t._2), None)))
  }

  def this(pairTuples: OddAccount) {
    this(pairTuples.map(t => (t._1, None, Some(t._2))))
  }
}
