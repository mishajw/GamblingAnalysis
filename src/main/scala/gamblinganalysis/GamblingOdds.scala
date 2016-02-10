package gamblinganalysis

class GamblingOdds(val odds: Seq[Odd]) {
  def getAllPossibilities(toBet: Int = 1) = {
    odds.map(_.getPossibilities(toBet))
  }

  def getAllProbabilities = {
    odds.map(_.getProbability)
  }

  def sumProbabilities = {
    getAllProbabilities.sum
  }

  override def toString: String = s"GamblingOdds(${odds.mkString(" | ")})"
}
