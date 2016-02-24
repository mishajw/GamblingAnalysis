package gamblinganalysis.retriever.odds

import gamblinganalysis.Sport
import gamblinganalysis.factory.{BookieFactory, GameFactory, SportFactory}
import gamblinganalysis.odds.{Odd, OddsCollection}
import gamblinganalysis.retriever.Retriever
import gamblinganalysis.util.exceptions.ParseException
import org.jsoup.nodes.Element
import play.api.Logger

/**
  * Created by misha on 08/02/16.
  */
object OddsCheckerRetriever extends Retriever {
  private val log = Logger(getClass)

  private val regexOdd = "(\\d+)/(\\d+)".r
  private val regexSimpleOdd = "(\\d+)".r

  private val attrSource = "title"
  private val attrOutcome = "data-bname"
  private val selTable = ".eventTable"
  private val selSource = s"td[data-bk] aside a"
  private val selOddRow = "tr[class=\"diff-row eventTableRow bc\"]"
  private val selOddCell = "td:not(.sel, .wo)"

  private val regexSport = ".*oddschecker.com/([A-Za-z]+)/.*".r

  def retrieve(url: String): OddsCollection = {
    val doc = getHtml(url)

    val sportString = url match {
      case regexSport(s) => s
      case _ => ""
    }

    val sport = SportFactory get sportString

    makeArray(doc.select(selTable)).toList match {
      case table :: xs => getOddsFromTable(table, sport)
      case _ => throw new ParseException("Couldn't find table")
    }
  }

  def getOddsFromTable(table: Element, sport: Sport): OddsCollection = {
    val sources = getSourcesFromTable(table)

    val rows = makeArray(table.select(selOddRow))

    val game = GameFactory get (rows.map(r => r.attr(attrOutcome)).toSet, sport)

    new OddsCollection(
      rows.map(oddRow => {
          val outcome = oddRow.attr(attrOutcome)
          getOddsFromRow(oddRow)
              .map(_ match {
                case Some((i1, i2)) => Some((i1, i2, outcome))
                case None => None
              })
        })
        .transpose
        .zipWithIndex
        .flatMap {
          case (os, i) =>
            os.map {
              case Some((i1, i2, outcome)) => Some(new Odd(i1, i2, outcome, game, BookieFactory get sources(i)))
              case None => None
            }
        }
        .flatten)
  }

  def getSourcesFromTable(table: Element): Seq[String] = {
    makeArray(table.select(selSource))
      .map(_.attr(attrSource))
  }

  def getOddsFromRow(row: Element): Seq[Option[(Int, Int)]] = {
    makeArray(row.select(selOddCell))
      .map(_.text() match {
        case regexOdd(a, b) => Some((a.toInt, b.toInt))
        case regexSimpleOdd(a) => Some((a.toInt, 1))
        case _ => None
      })
  }
}
