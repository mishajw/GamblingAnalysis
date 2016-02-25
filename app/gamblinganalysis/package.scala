import gamblinganalysis.odds.Odd

package object gamblinganalysis {
  case class Bookie(name: String)
  case class User(name: String)
  case class Game(outcomes: Set[String], sport: Sport)
  case class Sport(title: String)
  case class OddPair(odd: Odd, money: Option[BigDecimal], account: Option[Account])
  case class Account(name: User, var amount: BigDecimal, bookie: Bookie)

  val bookies = Seq(
    "Totesport", "Winner", "Stan James", "Paddy Power", "BetBright", "Matchbook",
    "10Bet", "Betfair", "Betdaq", "Boylesports", "Marathon Bet", "Unibet", "Ladbrokes",
    "888sport", "Bet 365", "Sportingbet", "Bwin", "William Hill", "Bet Victor",
    "Netbet UK", "Betway", "Betfair Sportsbook", "32Red Bet", "Sky Bet", "Coral",
    "Betfred"
  )

  val users = Seq(
    "Misha", "Hannah", "Jodie", "Mona", "Zoe", "Harry", "Joe", "Ali"
  )


  /**
    * Used so we can have multiple constructors that takes lists
    * (ffs scala)
    */
  case class OddMoney(list: Seq[(Odd, BigDecimal)])
  case class OddAccount(list: Seq[(Odd, Account)])

  implicit def om(list: Seq[(Odd, BigDecimal)]) = OddMoney(list)
  implicit def oa(list: Seq[(Odd, Account)]) = OddAccount(list)
}
