package gamblinganalysis

import gamblinganalysis.util.exceptions.ParseException
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

/**
  * Created by misha on 08/02/16.
  */
object OddsCheckerRetriever {

  private val url: String =
    "http://www.oddschecker.com/tennis/wta-st-petersburg/daria-kasatkina-v-laura-siegemund/winner"

  private val regexOdd = "(\\d+)/(\\d+)".r
  private val regexSimpleOdd = "(\\d+)".r

  private val attrSource = "data-bk"
  private val attrOutcome = "data-bname"
  private val selTable = ".eventTable"
  private val selSource = s".eventTableHeader td[$attrSource]"
  private val selOddRow = "tr[class=\"diff-row eventTableRow bc\"]"
  private val selOddCell = "td:not(.sel, .wo)"

  def getOdds: Seq[GamblingOdds] = {
    val doc = Jsoup.connect(url).userAgent("Mozilla").get()

    makeArray(doc.select(selTable)).toList match {
      case table :: xs => getOddsFromTable(table)
      case _ => throw new ParseException("Couldn't find table")
    }
  }

  def getOddsFromTable(table: Element): Seq[GamblingOdds] = {
    val sources = getSourcesFromTable(table)

    makeArray(table.select(selOddRow))
        .map(oddRow => {
          val outcome = oddRow.attr(attrOutcome)
          val odds = getOddsFromRow(oddRow)
              .map(_ match {
                case Some((i1, i2)) => Some((i1, i2, outcome))
                case None => None
              })

          println(odds)
          odds
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
        .map(os => new GamblingOdds(os.flatten))
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

  private def makeArray(es: Elements): Seq[Element] = es
    .toArray()
    .toSeq
    .map(_.asInstanceOf[Element])
    .asInstanceOf[Seq[Element]]
}
