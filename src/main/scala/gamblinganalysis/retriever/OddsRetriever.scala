package gamblinganalysis.retriever

import java.net.SocketTimeoutException

import gamblinganalysis.{Odd, OddsCollection}
import gamblinganalysis.util.exceptions.ParseException
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

/**
  * Created by misha on 08/02/16.
  */
object OddsRetriever extends Retriever {

  private val url: String =
    "http://www.oddschecker.com/football/spain/la-liga-primera/gijon-v-rayo-vallecano/winner"

  private val regexOdd = "(\\d+)/(\\d+)".r
  private val regexSimpleOdd = "(\\d+)".r

  private val attrSource = "title"
  private val attrOutcome = "data-bname"
  private val selTable = ".eventTable"
  private val selSource = s"td[data-bk] aside a"
  private val selOddRow = "tr[class=\"diff-row eventTableRow bc\"]"
  private val selOddCell = "td:not(.sel, .wo)"

  def getOdds: Seq[OddsCollection] = {
    try {
      val doc = getHtml(url)

      makeArray(doc.select(selTable)).toList match {
        case table :: xs => getOddsFromTable(table)
        case _ => throw new ParseException("Couldn't find table")
      }
    } catch {
      case e: SocketTimeoutException =>
        throw new ParseException("Couldn't parse because of failed internet connection")
    }
  }

  def getOddsFromTable(table: Element): Seq[OddsCollection] = {
    val sources = getSourcesFromTable(table)

    makeArray(table.select(selOddRow))
        .map(oddRow => {
          val outcome = oddRow.attr(attrOutcome)
          getOddsFromRow(oddRow)
              .map(_ match {
                case Some((i1, i2)) => Some((i1, i2, outcome))
                case None => None
              })
        })
        .transpose
        .zipWithIndex
        .map {
          case (os, i) =>
            os.map {
              case Some((i1, i2, outcome)) => Some(new Odd(i1, i2, outcome, sources(i)))
              case None => None
            }
        }
        .map(os => new OddsCollection(os.flatten))
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
