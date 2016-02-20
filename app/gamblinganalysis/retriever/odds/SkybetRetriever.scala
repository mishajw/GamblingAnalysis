package gamblinganalysis.retriever.odds

import gamblinganalysis.factory.{GameOutcomeFactory, GameFactory, BookieFactory}
import gamblinganalysis.odds.{Odd, OddsCollection}
import gamblinganalysis.retriever.Retriever
import org.jsoup.nodes.Element

/**
  * Created by misha on 15/02/16.
  */
object SkybetRetriever extends Retriever {
  private val source = BookieFactory get "Skybet"
  private val baseUrl = "http://www.skybet.com/"

  private val sports = Seq("tennis")

  private val regexTeams = "(.*) vs? (.*)".r
  private val regexOdds = "(\\d+)/(\\d+)".r

  private val selTable = "div[data-class-name]"
  private val selRow = ".live-rowgroup"
  private val selOdds = ".odds"

  def run(url: String = baseUrl + sports.head): Seq[OddsCollection] = {
    val html = getHtml(url)

    val tables = getTables(html)
    val rows = tables.flatMap(t => {
      getRowsFromTable(t)
        .map(r => {
          (t.attr("data-class-name"), r)
        })
    })

    rows.flatMap { case (t, r) =>
      getOddsFromRow(r, t)
    }
  }

  def getTables(html: Element) = {
    makeArray(html.select(selTable))
  }

  def getRowsFromTable(table: Element) = {
    makeArray(table.select(selRow))
  }

  def getOddsFromRow(row: Element, sport: String): Option[OddsCollection] = {
    // Parse teams
    row.attr("data-event-title") match {
      case regexTeams(t1, t2) =>
        // Parse odds
        makeArray(row.select(selOdds)).map(_.text()) match {
          // Win/Draw/Lose
          case Seq(regexOdds(w1n, w1d), regexOdds(dn, dd), regexOdds(w2n, w2d)) =>
            val game = GameFactory get Set(t1, "Draw", t2)
            Some(new OddsCollection(Seq(
              new Odd(w1n.toInt, w1d.toInt, GameOutcomeFactory get (t1, game), source, sport),
              new Odd(dn.toInt, dd.toInt, GameOutcomeFactory get ("Draw", game), source, sport),
              new Odd(w2n.toInt, w2d.toInt, GameOutcomeFactory get (t2, game), source, sport)
            )))
          // Win/Lose
          case Seq(regexOdds(w1n, w1d), regexOdds(w2n, w2d)) =>
            val game = GameFactory get Set(t1, t2)
            Some(new OddsCollection(Seq(
              new Odd(w1n.toInt, w1d.toInt, GameOutcomeFactory get (t1, game), source, sport),
              new Odd(w2n.toInt, w2d.toInt, GameOutcomeFactory get (t2, game), source, sport)
            )))
          case x =>
            println(s"Couldn't parse $x with teams $t1 and $t2")
            None
        }
      case x =>
        println(s"Couldn't parse $x")
        None
    }
  }
}
