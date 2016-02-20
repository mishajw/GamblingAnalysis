package gamblinganalysis.retriever.odds

import gamblinganalysis.factory.BookieFactory
import gamblinganalysis.odds.{OddsCollection, Odd}
import gamblinganalysis.retriever.Retriever
import gamblinganalysis.util.exceptions.ParseException
import org.jsoup.nodes.Element

/**
  * Created by misha on 08/02/16.
  */
object OddsCheckerRetriever extends Retriever {

  private val regexOdd = "(\\d+)/(\\d+)".r
  private val regexSimpleOdd = "(\\d+)".r

  private val attrSource = "title"
  private val attrOutcome = "data-bname"
  private val selTable = ".eventTable"
  private val selSource = s"td[data-bk] aside a"
  private val selOddRow = "tr[class=\"diff-row eventTableRow bc\"]"
  private val selOddCell = "td:not(.sel, .wo)"

  def getOdds(url: String): Seq[OddsCollection] = {
    val doc = getHtml(url)

    makeArray(doc.select(selTable)).toList match {
      case table :: xs => getOddsFromTable(table)
      case _ => throw new ParseException("Couldn't find table")
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
              case Some((i1, i2, outcome)) => Some(new Odd(i1, i2, outcome, BookieFactory get sources(i)))
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
