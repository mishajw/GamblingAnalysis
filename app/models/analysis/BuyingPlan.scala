package models.analysis

import models.odds.Odd
import models.util.JsonConvertable
import models.{Account, OddAccount, OddMoney, OddPair}
import org.json4s._
import org.json4s.native.JsonParser

import scala.math.BigDecimal.RoundingMode

class BuyingPlan(pairTuples: Seq[(Odd, Option[BigDecimal], Option[Account])]) extends JsonConvertable {

  /**
    * Details of what to buy with that account
    */
  val pairs: Seq[OddPair] = pairTuples map {
    case (odd, mon, acc) => OddPair(odd, mon, acc)
  }

  /**
    * Take in only money
 *
    * @param pairTuples money to put on each odd
    */
  def this(pairTuples: OddMoney) {
    this(pairTuples.list.map(t => (t._1, Some(t._2), None)))
  }

  /**
    * Take in only account
 *
    * @param pairTuples account to use for each odd
    */
  def this(pairTuples: OddAccount) {
    this(pairTuples.list.map(t => (t._1, None, Some(t._2))))
  }

  /**
    * All odds
    */
  lazy val odds = pairs map (_.odd)

  /**
    * Options of all money and accounts
    */
  private lazy val maybeMoney = pairs map (_.money)
  private lazy val maybeAccounts = pairs map (_.account)

  /**
    * Actual money and accounts
    */
  lazy val money = maybeMoney.flatten
  lazy val accounts = maybeAccounts.flatten

  /**
    * Whether or not the money/accounts/both are complete
    */
  lazy val moneyComplete = money.size == maybeMoney.size
  lazy val accountsComplete = accounts.size == maybeAccounts.size
  lazy val complete = moneyComplete && accountsComplete

  /**
    * Total cost going into this plan
    */
  lazy val totalCost = money.sum

  /**
    * All possible outcomes
    */
  lazy val possibleOutcomes = {
    if (!moneyComplete) Seq() else

    pairs map { case OddPair(odd, money, account) =>
      odd.getInclusiveProfit(money.get) - totalCost
    }
  }

  /**
    * (Minimum) return on investment
    */
  lazy val roi = {
    if (!moneyComplete) BigDecimal(0) else
    possibleOutcomes.min / totalCost
  }

  /**
    * (Minimum) profit from the plan
    */
  lazy val profit = possibleOutcomes.min

  /**
    * The game betting on
    */
  lazy val game = {
    val set = odds.map(_.game).toSet
    if (set.size == 1) {
      set.head
    } else {
      throw new IllegalArgumentException("Buying plan spans different games")
    }
  }

  /**
    * Percentage to a string
 *
    * @param bd big decimal of percentage
    * @return percentage string
    */
  private def parsePercentage(bd: BigDecimal) = s"${bd.setScale(2, RoundingMode.DOWN) * 100}%"

  /**
    * Money to a string
 *
    * @param bd big decimal of amount of money
    * @return money string
    */
  private def parseMoney(bd: BigDecimal) = s"£${bd.setScale(2, RoundingMode.DOWN)}"

  /**
    * List of money to a formatted string
 *
    * @param bds the money list
    * @return the formatted money string
    */
  private def parseMoneyList(bds: Seq[BigDecimal]) = bds.map(parseMoney).mkString(", ")

  override def toJson = {
    JObject(List(
      "pairs" -> JArray(pairs.toList.map({ op =>
        JObject(List(
          "odd" -> op.odd.toJson,
          "money" -> JDecimal(op.money.getOrElse(0)),
          "account" -> op.odd.toJson
        ))
      })),
      "roi" -> JDecimal(roi)
    ))
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
}
