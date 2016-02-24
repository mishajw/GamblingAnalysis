package gamblinganalysis.retriever.odds

import gamblinganalysis.{Sport, Game}
import gamblinganalysis.factory.{SportFactory, BookieFactory, GameFactory, GameOutcomeFactory}
import gamblinganalysis.odds.{Odd, OddsCollection}
import org.jsoup.nodes.Element
import play.api.Logger

/**
  * Created by misha on 15/02/16.
  */
object SkybetRetriever extends OddsRetriever {
  private val log = Logger(getClass)

  private val source = BookieFactory get "Skybet"
  private val baseUrl = "http://www.skybet.com/"
  private val sports = Seq("tennis")

  private val regexTeams = "(.*) vs? (.*)".r
  private val regexOdds = "(\\d+)/(\\d+)".r

  private val selTable = "div[data-class-name]"
  private val selRow = ".live-rowgroup"
  private val selOdds = ".odds"

  override def retrieve(): OddsCollection = {
    val url = baseUrl + sports.head
    val html = getHtml(url)

    val tables = getTables(html)
    val rows = tables.flatMap(t => {
      getRowsFromTable(t)
        .map(r => {
          (t.attr("data-class-name"), r)
        })
    })

    val odds = rows.flatMap({ case (t, r) =>
      getOddsFromRow(r, t)
    }).flatMap(_.odds)

    new OddsCollection(odds)
  }

  def getTables(html: Element) = {
    makeArray(html.select(selTable))
  }

  def getRowsFromTable(table: Element) = {
    makeArray(table.select(selRow))
  }

  def getOddsFromRow(row: Element, sportString: String): Option[OddsCollection] = {
    val sport = SportFactory get sportString

    // Parse teams
    row.attr("data-event-title") match {
      case regexTeams(t1, t2) =>
        // Parse odds
        makeArray(row.select(selOdds)).map(_.text()) match {
          // Win/Draw/Lose
          case Seq(regexOdds(w1n, w1d), regexOdds(dn, dd), regexOdds(w2n, w2d)) =>
            val game = GameFactory get (Set(t1, "Draw", t2), sport)
            Some(new OddsCollection(Seq(
              new Odd(w1n.toInt, w1d.toInt, GameOutcomeFactory get (t1, game), source),
              new Odd(dn.toInt, dd.toInt, GameOutcomeFactory get ("Draw", game), source),
              new Odd(w2n.toInt, w2d.toInt, GameOutcomeFactory get (t2, game), source)
            )))
          // Win/Lose
          case Seq(regexOdds(w1n, w1d), regexOdds(w2n, w2d)) =>
            val game = GameFactory get (Set(t1, t2), sport)
            Some(new OddsCollection(Seq(
              new Odd(w1n.toInt, w1d.toInt, GameOutcomeFactory get (t1, game), source),
              new Odd(w2n.toInt, w2d.toInt, GameOutcomeFactory get (t2, game), source)
            )))
          case x =>
            info.warn(s"Couldn't parse $x with teams $t1 and $t2")
            None
        }
      case x =>
        info.warn(s"Couldn't parse $x")
        None
    }
  }
}
