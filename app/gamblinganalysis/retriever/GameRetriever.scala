package gamblinganalysis.retriever

import gamblinganalysis.analysis.OddsOptimiser
import gamblinganalysis.retriever.odds.OddsCheckerRetriever
import gamblinganalysis.util.exceptions.ParseException
import org.jsoup.nodes.Element
import play.api.Logger

/**
  * Created by misha on 12/02/16.
  */
object GameRetriever extends Retriever {
  private val log = Logger(getClass)

  private val baseUrl = "http://www.oddschecker.com/"
  private val sports = Seq("football", "tennis", "snooker/welsh-open")

  private val selGameRow = "tr.match-on"
  private val selInPlay = "td.betting a.in-play"

  def retrieve = {
    sports
      .map(baseUrl + _)
      .map(getHtml(_))
      .map(getRows)
      .map(filterForInPlay)
      .flatMap(getLinksFromRows)
  }

  private def getRows(doc: Element): Seq[Element] = {
    makeArray(doc.select(selGameRow))
  }

  private def filterForInPlay(es: Seq[Element]): Seq[Element] = {
    es.filterNot(e => e.select(selInPlay).isEmpty)
  }

  private def getLinksFromRows(es: Seq[Element]): Seq[String] = {
    es.map(e =>
      makeArray(e.select(selInPlay))
          .map(_.attr("href"))
          .map(baseUrl + _)
          .head
    )
  }

  def main(args: Array[String]) {
    log.info("Starting GameRetriever")

    GameRetriever.retrieve.flatMap(g => {
      try {
        val odds = OddsCheckerRetriever.getOdds(g)
        val optimum = OddsOptimiser.optimise(odds)
        Some(optimum, optimum.getInvestmentReturn)
      } catch {
        case e: Exception => None
        case e: ParseException => None
      }
    })
      .sortBy { case (odds, ir) => ir }
      .map { case (odds, ir) => odds }
      .foreach(o => {
        o.printSafeBet()
        println()
      })
  }
}
