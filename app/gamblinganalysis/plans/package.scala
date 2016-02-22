package gamblinganalysis

import gamblinganalysis.odds.Odd

package object plans {
  abstract class OddPair(val odd: Odd)

  class GenericPlan[T <: OddPair](val oddPairs: Seq[T]) {
    lazy val odds = oddPairs.map(_.odd)

    def printPlan(): Unit = {}
  }

  type Plan = GenericPlan[OddPair]
  type ValuedPlan = GenericValuedPlan[ValuedOdd]
}
