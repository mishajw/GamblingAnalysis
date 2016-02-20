package object gamblinganalysis {
  case class Bookie(name: String)
  case class AccountOwner(name: String)
  case class Game(outcomes: Set[String])
  case class GameOutcome(outcome: String, game: Game)
}
