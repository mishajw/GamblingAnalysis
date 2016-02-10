package gamblinganalysis

/**
  * Created by misha on 09/02/16.
  */
class Odd(val gains: Int, val base: Int, val title: String) {
  def getPossibilities(toBet: Int): (Double, Double) = {
    (((toBet.toDouble / base.toDouble) * gains.toDouble) + toBet.toDouble, -toBet.toDouble)
  }

  def getProbability: Double = {
    base.toDouble / (gains.toDouble + base.toDouble)
  }

  override def toString: String =
    s"Odd($title => $gains/$base)"

}
