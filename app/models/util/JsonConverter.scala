package models.util

import models.Account
import models.analysis.BuyingPlan
import models.odds.Odd
import org.json4s.JsonAST.{JDecimal, JString, JObject}
import org.json4s._

object JsonConverter {
  def fromOdd(odd: Odd) = {
    JObject(List(
      "numerator" -> JInt(odd.gains),
      "denominator" -> JInt(odd.base),
      "bookie" -> JString(odd.bookie.name),
      "outcome" -> JString(odd.outcome)
    ))
  }

  def fromAccount(account: Account) = {
    JObject(List(
      "user" -> JString(account.name.name),
      "amount" -> JDecimal(account.amount),
      "bookie" -> JString(account.bookie.name)
    ))
  }

  def fromBuyingPlan(plan: BuyingPlan) = {
    JObject(List(
      "pairs" -> JArray(plan.pairs.toList.map({ op =>
        JObject(List(
          "odd" -> fromOdd(op.odd),
          "money" -> JDecimal(op.money.getOrElse(0)),
          "account" -> fromAccount(op.account.get)
        ))
      })),
      "roi" -> JDecimal(plan.roi)
    ))
  }
}
