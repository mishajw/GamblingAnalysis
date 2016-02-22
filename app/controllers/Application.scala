package controllers

import gamblinganalysis.odds.OddsCollection
import org.json4s._
import org.json4s.jackson.JsonMethods

import gamblinganalysis.analysis.OddsOptimiser
import gamblinganalysis.retriever.GameRetriever
import gamblinganalysis.retriever.odds.OddsCheckerRetriever
import gamblinganalysis.util.exceptions.ParseException
import play.api.mvc._

object Application extends Controller {

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def bestOdds = Action {
    val bestOdds = GameRetriever.retrieve.flatMap(g => {
      try {
        val odds = OddsCheckerRetriever.getOdds(g)
        Some(OddsOptimiser.optimise(odds))
      } catch {
        case e: Exception => None
        case e: ParseException => None
      }
    })

    val json = JObject(List(
      "oddsCollection" ->
        JArray(bestOdds.toList.map(parseOddsCollection))
    ))

    Ok(JsonMethods.pretty(JsonMethods.render(json)))
  }

  private def parseOddsCollection(oc: OddsCollection) = {
    JObject(List(
      "odds" -> JArray(oc.odds.toList.map(o => {
        JObject(List(
          "numerator" -> JInt(o.gains),
          "denominator" -> JInt(o.base),
          "bookie" -> JString(o.bookie.name),
          "outcome" -> JString(o.outcome)
        ))
      })),
      "roi" -> JDecimal(oc.getInvestmentReturn)
    ))
  }
}