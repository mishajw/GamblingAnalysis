package gamblinganalysis

import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

/**
  * Created by misha on 08/02/16.
  */
object Main {

  private val url: String = "http://sports.williamhill.com/bet/en-gb/betting/y/5/tm/Football.html"

  val regexOdd = "(\\d+)/(\\d+)".r

  val eventSelector = ".rowLive, .rowOdd"
  val oddSelector = ".eventprice"

  def main(args: Array[String]) = {
    val doc = Jsoup.connect(url).get()

    val allOdds = makeArray(doc.select(eventSelector))
        .flatMap(e => {
          val odds = makeArray(e.select(oddSelector))
              .flatMap(o => o.text() match {
                case regexOdd(s1, s2) => Some(new Odd(s1.toInt, s2.toInt))
                case x => println(x) ; None
              }).toSeq

          if (odds.size == 3)
            Some(new GamblingOdds(odds))
          else None
        }).toSeq

    println(allOdds
      .map(o => s"$o => ${o.sumProbabilities})")
      .mkString("\n"))
  }

  private def makeArray(es: Elements) = es
    .toArray()
    .map(_.asInstanceOf[Element])
    .asInstanceOf[Array[Element]]
}
