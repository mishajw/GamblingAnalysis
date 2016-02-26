package test

import models._
import models.analysis.BuyingPlan
import models.odds.Odd

/**
  * Created by misha on 26/02/16.
  */
object TestObjects {
  val game = Game(Set("a", "b", "c"), Sport("a-z"))

  val bookie1: Bookie = Bookie("bookie")
  val bookie2: Bookie = Bookie("book keeper")
  val bookie3: Bookie = Bookie("prick")

  val odd1 = new Odd(3, 1, "a", game, bookie1)
  val odd2 = new Odd(5, 1, "b", game, bookie2)
  val odd3 = new Odd(2, 1, "c", game, bookie3)

  val acc1 = Account(User("Misha"), BigDecimal(11), bookie1)
  val acc2 = Account(User("Hannah"), BigDecimal(10), bookie2)
  val acc3 = Account(User("Jodie"), BigDecimal(12), bookie3)

  val buyingPlan = new BuyingPlan(Seq(
    (odd1, Some(BigDecimal(3)), Some(acc1)),
    (odd2, Some(BigDecimal(2)), Some(acc2)),
    (odd3, Some(BigDecimal(4)), Some(acc3))
  ))
}
