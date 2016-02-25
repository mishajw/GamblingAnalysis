package models.odds

import models.{Bookie, Game}
import play.api.Logger

class OddsCollection(val odds: Seq[Odd]) {
  private val log = Logger(getClass)

  def forBookie(bookie: Bookie) = {
    odds filter (_.bookie == bookie)
  }

  def forGame(game: Game) = {
    odds filter (_.game == game)
  }

  def forOutcome(outcome: String, game: Game) = {
    odds filter (o => o.outcome == outcome && o.game == game)
  }

  def groupedOutcome() = {
    odds
      .map(o => (o.outcome, o.game))
      .distinct
      .map { case (o, g) => (o, forOutcome(o, g)) }
      .toMap
  }

  override def toString: String = s"OddsCollection(${odds.mkString(" | ")})"
}
