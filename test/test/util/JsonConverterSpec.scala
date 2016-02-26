package test.util

import models.util.JsonConverter._
import org.junit.runner._
import org.specs2.mutable._
import org.specs2.runner._
import test.TestObjects._

@RunWith(classOf[JUnitRunner])
class JsonConverterSpec extends Specification {
  "JsonConverter" should {
    "convert odds successfully" in {
      odd1 must equalTo(toOdd(fromOdd(odd1)))
    }

    "convert games successfully" in {
      game must equalTo(toGame(fromGame(game)))
    }

    "convert accounts successfully" in {
      acc1 must equalTo(toAccount(fromAccount(acc1)))
    }

    "convert buying plans successfully" in {
      buyingPlan must equalTo(toBuyingPlan(fromBuyingPlan(buyingPlan)))
    }
  }
}
