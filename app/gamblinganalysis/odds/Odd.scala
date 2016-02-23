package gamblinganalysis.odds

import gamblinganalysis.{Sport, Bookie, GameOutcome}

/**
  * Created by misha on 09/02/16.
  */
class Odd(val gains: Int, val base: Int, val gameOutcome: GameOutcome, val bookie: Bookie) {
  def getPossibilities(toBet: Int): (BigDecimal, BigDecimal) = {
    (((toBet.toDouble / base.toDouble) * gains.toDouble) + toBet.toDouble, -toBet.toDouble)
  }

  def getProbability: BigDecimal = {
    base.toDouble / (gains.toDouble + base.toDouble)
  }

  def getProfit(bet: BigDecimal): BigDecimal = {
    (bet / base) * gains
  }

  def getInclusiveProfit(bet: BigDecimal): BigDecimal = {
    getProfit(bet) + bet
  }

  def betAmount(amount: BigDecimal): (BigDecimal, BigDecimal) = {
    (amount * gains, amount * base)
  }

  def oddsString = {
    s"$gains/$base"
  }

  def outcome = gameOutcome.outcome

  override def toString: String =
    s"Odd($bookie - $gameOutcome => $gains/$base)"
}
