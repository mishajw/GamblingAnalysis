package gamblinganalysis

import gamblinganalysis.util.exceptions.ParseException
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

/**
  * Created by misha on 08/02/16.
  */
object Main {

  private val url: String = "http://www.oddschecker.com/football/english/fa-cup/peterborough-v-west-brom/to-qualify"

  val regexOdd = "(\\d+)/(\\d+)".r
  val regexSimpleOdd = "(\\d+)".r

  val attrSource = "data-bk"
  val attrOutcome = "data-bname"
  val selTable = ".eventTable"
  val selSource = s".eventTableHeader td[$attrSource]"
  val selOddRow = "tr[class=\"diff-row eventTableRow bc\"]"
  val selOddCell: String = "td:not(.sel, .wo)"

  def main(args: Array[String]) = {
    val doc = Jsoup.connect(url).userAgent("Mozilla").get()

    val allOdds = makeArray(doc.select(selTable)).toList match {
      case table :: xs => getOddsFromTable(table)
      case _ => throw new ParseException("Couldn't find table")
    }

    println(allOdds.mkString("\n"))
  }

  def getOddsFromTable(table: Element): Seq[GamblingOdds] = {
    val sources = getSourcesFromTable(table)

    makeArray(table.select(selOddRow))
        .map(oddRow => {
          val outcome = oddRow.attr(attrOutcome)
          getOddsFromRow(oddRow)
              .map(_ match {
                case Some(o) => Some(new Odd(o.gains, o.base, Some(outcome)))
                case None => None
              })
        })
        .transpose
        .zipWithIndex
        .map { case (os, i) => new GamblingOdds(os.flatten, sources(i)) }
  }

  def getSourcesFromTable(table: Element): Seq[String] = {
    makeArray(table.select(selSource))
      .map(_.attr(attrSource))
  }

  def getOddsFromRow(row: Element): Seq[Option[Odd]] = {
    makeArray(row.select(selOddCell))
      .map(_.text() match {
        case regexOdd(a, b) => Some(new Odd(a.toInt, b.toInt))
        case regexSimpleOdd(a) => Some(new Odd(a.toInt, 1))
        case _ => None
      })
  }

  private def makeArray(es: Elements): Seq[Element] = es
    .toArray()
    .toSeq
    .map(_.asInstanceOf[Element])
    .asInstanceOf[Seq[Element]]
}
