package gamblinganalysis.odds

import scala.math.BigDecimal.RoundingMode

class OddsCollection(val odds: Seq[Odd]) {
  def getAllPossibilities(toBet: Int = 1) = {
    odds.map(_.getPossibilities(toBet))
  }

  def getAllProbabilities = {
    odds.map(_.getProbability)
  }

  def getNormalisedProbabilities = {
    val probabilities: Seq[BigDecimal] = getAllProbabilities
    val total = probabilities.sum
    probabilities.map(p => p / total)
  }

  def sumProbabilities = {
    getAllProbabilities.sum
  }

  def betSafely() = {
    val allProbs = getNormalisedProbabilities.map(_ * 100).map(_.setScale(2, RoundingMode.FLOOR))
    val gains = betWith(allProbs).map(_.setScale(2, RoundingMode.FLOOR))

    val minimumGain: BigDecimal = gains.min

    println(
      s"From:      ${odds.map(_.source).mkString(", ")}\n" +
      s"Bet on:    ${odds.map(_.title).mkString(", ")}\n" +
      s"Odds:      ${odds.map(_.oddsString).mkString(", ")}\n" +
      s"Put on:    ${allProbs.mkString(", ")}\n" +
      s"Gives you: ${gains.mkString(", ")}\n" +
      f"Investment return: ${(minimumGain / allProbs.sum) * 100}%.2f%%")

    minimumGain
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

  override def toString: String = s"OddsCollection(${odds.mkString(" | ")})"
}
