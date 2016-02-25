package models.util.db

import models.{Account, Bookie, User}
import org.json4s.JsonAST._
import org.json4s.native.JsonParser

import scala.io.Source

/**
  * Created by misha on 25/02/16.
  */
object DBInitializer {
  def fillWithJson() = {
    val json = JsonParser.parse(Source.fromFile("res/json/start-up.json").mkString)

    val accounts: Seq[Account] = for (
      JObject(obj) <- json;
      JField("accounts", JArray(accounts)) <- obj;
      JObject(fields) <- accounts;
      JField("user", JString(user)) <- fields;
      JField("bookie", JString(bookie)) <- fields;
      JField("amount", JDouble(amount)) <- fields
    ) yield Account(User(user), amount, Bookie(bookie))

    accounts foreach UserDBHandler.insertAccount
  }
}
