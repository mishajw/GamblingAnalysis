package gamblinganalysis.analysis

import gamblinganalysis.accounts.Account
import gamblinganalysis.odds.Odd
import gamblinganalysis.util.JsonConvertable
import gamblinganalysis.{OddAccount, OddMoney}
import org.json4s._

import scala.math.BigDecimal.RoundingMode

class BuyingPlan(pairTuples: Seq[(Odd, Option[BigDecimal], Option[Account])]) extends JsonConvertable {

  case class OddPair(odd: Odd, money: Option[BigDecimal], account: Option[Account])

  val pairs: Seq[OddPair] = pairTuples map {
    case (odd, mon, acc) => OddPair(odd, mon, acc)
  }

  def this(pairTuples: OddMoney) {
    this(pairTuples.list.map(t => (t._1, Some(t._2), None)))
  }

  def this(pairTuples: OddAccount) {
    this(pairTuples.list.map(t => (t._1, None, Some(t._2))))
  }

  lazy val odds = pairs map (_.odd)
  private lazy val maybeMoney = pairs map (_.money)
  private lazy val maybeAccounts = pairs map (_.account)

  lazy val money = maybeMoney.flatten
  lazy val accounts = maybeAccounts.flatten

  lazy val moneyComplete = money.size == maybeMoney.size
  lazy val accountsComplete = accounts.size == maybeAccounts.size

  lazy val totalCost = money.sum

  lazy val possibleOutcomes = {
    if (!moneyComplete) Seq() else

    pairs map { case OddPair(odd, money, account) =>
      odd.getInclusiveProfit(money.get) - totalCost
    }
  }

  lazy val roi = {
    if (!moneyComplete) BigDecimal(0) else
    possibleOutcomes.min / totalCost
  }

  override def toString: String = {
    s"Outcomes:   ${odds.map(_.outcome).mkString(", ")}" +
    s"\nBookies:    ${odds.map(_.bookie).mkString(", ")}" +
    s"\nOdds:       ${odds.map(_.oddsString).mkString(", ")}" + {
      if (!moneyComplete) "" else
        s"\nMoney:      ${parseMoneyList(money)}" +
        s"\nOutcomes:   ${parseMoneyList(possibleOutcomes)}" +
        s"\nROI:        ${parsePercentage(roi)}"
    } + {
      if (!accountsComplete) "" else
        s"\nAccounts:   ${accounts.map(_.name).mkString(",")}"
    }
  }

  private def parsePercentage(bd: BigDecimal) = s"${bd.setScale(2, RoundingMode.DOWN)}%"

  private def parseMoney(bd: BigDecimal) = s"£${bd.setScale(2, RoundingMode.DOWN)}"

  private def parseMoneyList(bds: Seq[BigDecimal]) = bds.map(parseMoney).mkString(", ")

  override def toJson = {
    JObject(List(
      "odds" -> JArray(odds.toList.map(o => {
        JObject(List(
          "numerator" -> JInt(o.gains),
          "denominator" -> JInt(o.base),
          "bookie" -> JString(o.bookie.name),
          "outcome" -> JString(o.outcome)
        ))
      })),
      "roi" -> JDecimal(roi)
    ))
  }
}
