package object gamblinganalysis {
  case class Bookie(name: String)
  case class AccountOwner(name: String)
  case class Game(outcomes: Set[String])
  case class GameOutcome(outcome: String, game: Game)

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
}
