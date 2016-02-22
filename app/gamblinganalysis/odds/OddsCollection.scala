package gamblinganalysis.odds

import gamblinganalysis.{Bookie, Game, GameOutcome}
import play.api.Logger

class OddsCollection(val odds: Seq[Odd]) {
  private val log = Logger(getClass)

  def forBookie(bookie: Bookie) = {
    odds filter (_.bookie == bookie)
  }

  def forGame(game: Game) = {
    odds filter (_.gameOutcome.game == game)
  }

  def forOutcome(outcome: GameOutcome) = {
    odds filter (_.gameOutcome == outcome)
  }

  def groupedOutcome() = {
    odds
      .map(_.gameOutcome)
      .distinct
      .map { o => (o, forOutcome(o)) }
      .toMap
  }

  override def toString: String = s"OddsCollection(${odds.mkString(" | ")})"
}
