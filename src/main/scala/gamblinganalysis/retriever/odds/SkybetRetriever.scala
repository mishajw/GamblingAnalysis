package gamblinganalysis.retriever.odds

import gamblinganalysis.retriever.Retriever
import gamblinganalysis.util.exceptions.ParseException
import gamblinganalysis.{Odd, OddsCollection}
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

/**
  * Created by misha on 15/02/16.
  */
object SkybetRetriever extends Retriever {
  private val source = "Skybet"
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
    val rows = tables.flatMap(getRowsFromTable)

    rows.flatMap(getOddsFromRow)
  }

  def getTables(html: Element) = {
    makeArray(html.select(selTable))
  }

  def getRowsFromTable(table: Element) = {
    makeArray(table.select(selRow))
  }

  def getOddsFromRow(row: Element): Option[OddsCollection] = {
    // Parse teams
    row.attr("data-event-title") match {
      case regexTeams(t1, t2) =>
        // Parse odds
        makeArray(row.select(selOdds)).map(_.text()) match {
          // Win/Draw/Lose
          case Seq(regexOdds(w1n, w1d), regexOdds(dn, dd), regexOdds(w2n, w2d)) =>
            Some(new OddsCollection(Seq(
              new Odd(w1n.toInt, w1d.toInt, t1, source),
              new Odd(dn.toInt, dd.toInt, "Draw", source),
              new Odd(w2n.toInt, w2d.toInt, t2, source)
            )))
          // Win/Lose
          case Seq(regexOdds(w1n, w1d), regexOdds(w2n, w2d)) =>
            Some(new OddsCollection(Seq(
              new Odd(w1n.toInt, w1d.toInt, t1, source),
              new Odd(w2n.toInt, w2d.toInt, t2, source)
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
