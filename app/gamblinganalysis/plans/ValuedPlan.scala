package gamblinganalysis.plans

import gamblinganalysis.odds.Odd
import play.api.Logger

case class ValuedOdd(private val odd: Odd, amount: BigDecimal)
    extends OddPair(odd)

class GenericValuedPlan[T <: ValuedOdd](oddPairs: Seq[T])
    extends GenericPlan[T](oddPairs) {
  private val log = Logger(getClass)

  lazy val amounts = oddPairs.map(_.amount)
  lazy val totalBet = oddPairs.map(_.amount).sum
  lazy val roi = possibleProfits.min / totalBet
  lazy val possibleProfits: Seq[BigDecimal] =
    oddPairs map { case ValuedOdd(odd, mon) =>
      odd.getInclusiveProfit(mon) - totalBet
    }

  override def printPlan() = {
    val planString = oddPairs.map({ case ValuedOdd(odd, mon) =>
      s"On: $odd" +
        s"\tAmount: $mon\n"
    }).mkString("\n")

    log.info(planString +
      s"\nGives possible profits of ${possibleProfits.mkString(", ")}")
  }
}

