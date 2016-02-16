package gamblinganalysis.odds

/**
  * Created by misha on 09/02/16.
  */
class Odd(val gains: Int, val base: Int, val title: String, val source: String, val sport: String = "") {
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

  override def toString: String =
    s"Odd($sport - $source - $title => $gains/$base)"

}