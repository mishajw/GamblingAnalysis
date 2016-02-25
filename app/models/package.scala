import models.odds.Odd
import models.util.JsonConvertable
import org.json4s.JsonAST.{JString, JDecimal, JObject}

package object models {
  case class Bookie(name: String)
  case class User(name: String)
  case class Game(outcomes: Set[String], sport: Sport)
  case class Sport(title: String)
  case class OddPair(odd: Odd, money: Option[BigDecimal], account: Option[Account])
  case class Account(name: User, var amount: BigDecimal, bookie: Bookie) extends JsonConvertable {
    override def toJson: JObject = JObject(List(
      "user" -> JString(name.name),
      "amount" -> JDecimal(amount),
      "bookie" -> JString(bookie.name)
    ))
  }

  /**
    * Used so we can have multiple constructors that takes lists
    * (ffs scala)
    */
  case class OddMoney(list: Seq[(Odd, BigDecimal)])
  case class OddAccount(list: Seq[(Odd, Account)])

  implicit def om(list: Seq[(Odd, BigDecimal)]) = OddMoney(list)
  implicit def oa(list: Seq[(Odd, Account)]) = OddAccount(list)
}
