package gamblinganalysis

import scala.math.BigDecimal.RoundingMode

class GamblingOdds(val odds: Seq[Odd], val source: String) {
  def getAllPossibilities(toBet: Int = 1) = {
    odds.map(_.getPossibilities(toBet))
  }

  def getAllProbabilities = {
    odds.map(_.getProbability)
  }

  def sumProbabilities = {
    getAllProbabilities.sum
  }

  def betSafely() = {
    val allProbs = getAllProbabilities.map(_ * 100).map(_.setScale(2, RoundingMode.FLOOR))
    val gains = betWith(allProbs).map(_.setScale(2, RoundingMode.FLOOR))

    println(
      s"Odds:      ${odds.mkString(", ")}\n" +
      s"Put on:    ${allProbs.mkString(", ")}\n" +
      s"Gives you: ${gains.mkString(", ")}")
  }

  def betWith(values: Seq[BigDecimal]): Seq[BigDecimal] = {
    if (values.size != odds.size) {
      throw new IllegalArgumentException(s"Passed ${values.size} values, should have ${odds.size}")
    }

    val totalBet = values.sum

    for (i <- values.indices) yield {
      odds(i).getInclusiveProfit(values(i)) - totalBet
    }
  }

  override def toString: String = s"GamblingOdds($source => ${odds.mkString(" | ")})"
}
