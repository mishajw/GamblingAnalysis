package gamblinganalysis.accounts

import gamblinganalysis.odds.{Odd, OddsCollection}
import play.api.Logger

import scala.math.BigDecimal.RoundingMode

/**
  * Created by misha on 16/02/16.
  */
class BuyingPlan(val pairedOdds: Seq[(Account, Odd)]) {

  private val log = Logger(getClass)

  private lazy val accounts = pairedOdds.map(_._1)
  private lazy val odds = pairedOdds.map(_._2)
  private lazy val oddsCollection = new OddsCollection(odds)

  def profit: BigDecimal = {
    val (limitingAcc, limitingOdd) = getLimiters

    val scale = limitingAcc.amount / limitingOdd.getProbability
    val betAmount = oddsCollection.getAllProbabilities.map(_ * scale)
    val results = oddsCollection.betWith(betAmount)

    results.min.setScale(2, RoundingMode.DOWN)
  }

  def getLimitingAccount = getLimiters._1

  private def getLimiters = {
    pairedOdds.sortBy({ case (acc, odd) =>
      acc.amount / odd.getProbability
    }).head
  }

  override def toString: String = s"BuyingPlan(${pairedOdds.mkString(" | ")})"
}
