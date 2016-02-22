package gamblinganalysis.plans

import gamblinganalysis.accounts.Account
import gamblinganalysis.odds.Odd
import play.api.Logger

case class FullOdd(private val _odd: Odd, private val _amount: BigDecimal, account: Account)
  extends ValuedOdd(_odd, _amount)

class FullPlan(oddPairs: Seq[FullOdd])
  extends GenericValuedPlan[FullOdd](oddPairs) {

  private val log = Logger(getClass)

  lazy val isPossible: Boolean = oddPairs forall { case FullOdd(odd, mon, acc) =>
    acc.amount >= mon
  }

  override def printPlan() = {
    val planString = oddPairs.map({ case FullOdd(odd, acc, mon) =>
      s"With account $acc\n" +
        s"\tAmount: $mon\n" +
        s"\tOn: $odd"
    }).mkString("\n")

    log.info(planString +
      s"\nGives possible profits of ${possibleProfits.mkString(", ")}")
  }

  override def toString: String = s"BuyingPlan(${oddPairs.mkString(" | ")})"
}
