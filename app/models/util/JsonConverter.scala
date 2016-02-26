package models.util

import models._
import models.analysis.BuyingPlan
import models.odds.Odd
import org.json4s.JsonAST.{JDecimal, JString, JObject}
import org.json4s._

/**
  * Convert objects to and from JSON
  */
object JsonConverter {
  def fromOdd(odd: Odd) = {
    JObject(List(
      "numerator" -> JInt(odd.gains),
      "denominator" -> JInt(odd.base),
      "bookie" -> JString(odd.bookie.name),
      "outcome" -> JString(odd.outcome),
      "game" -> fromGame(odd.game)
    ))
  }

  def toOdd(json: JObject): Odd = {
    {for {
      JObject(obj) <- json
      JField("numerator", JInt(numerator)) <- obj
      JField("denominator", JInt(denominator)) <- obj
      JField("bookie", JString(bookie)) <- obj
      JField("outcome", JString(outcome)) <- obj
      JField("game", game: JObject) <- obj
    } yield new Odd(numerator.toInt, denominator.toInt, outcome, toGame(game), Bookie(bookie))}
        .asInstanceOf[List[Odd]]
        .head
  }

  def fromGame(game: Game): JObject = {
    JObject(List(
      "outcomes" -> JArray(game.outcomes.map(JString).toList),
      "sport" -> JString(game.sport.title)
    ))
  }

  def toGame(json: JObject): Game = {
    {for {
      JObject(obj) <- json
      JField("outcomes", JArray(outcomes: List[JString])) <- obj
      JField("sport", JString(sport)) <- obj
    } yield Game(outcomes.map({case JString(s) => s}).toSet, Sport(sport))}
        .asInstanceOf[List[Game]]
        .head
  }

  def fromAccount(account: Account) = {
    JObject(List(
      "user" -> JString(account.name.name),
      "amount" -> JDecimal(account.amount),
      "bookie" -> JString(account.bookie.name)
    ))
  }

  def toAccount(json: JObject): Account = {
    {for {
      JObject(obj) <- json
      JField("user", JString(user)) <- obj
      JField("amount", JDecimal(amount)) <- obj
      JField("bookie", JString(bookie)) <- obj
    } yield Account(User(user), amount, Bookie(bookie))}
        .asInstanceOf[List[Account]]
        .head
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

  def toBuyingPlan(json: JObject) = {
    {for {
      JObject(obj) <- json
      JField("pairs", JArray(pairs)) <- obj
    } yield {
      val odds = for {
        JObject(pair) <- pairs
        JField("odd", odd: JObject) <- pair
        JField("money", JDecimal(money)) <- pair
        JField("account", account: JObject) <- pair
      } yield (toOdd(odd), Some(money), Some(toAccount(account)))
      new BuyingPlan(odds)
    }}.asInstanceOf[List[BuyingPlan]]
      .head
  }
}
