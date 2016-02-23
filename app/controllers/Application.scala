package controllers

import gamblinganalysis.odds.OddsCollection
import org.json4s._
import org.json4s.jackson.JsonMethods

import gamblinganalysis.analysis.{BuyingPlan, OddsOptimiser}
import gamblinganalysis.retriever.GameRetriever
import gamblinganalysis.retriever.odds.OddsCheckerRetriever
import gamblinganalysis.util.exceptions.ParseException
import play.api.mvc._

object Application extends Controller {

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def bestOdds = Action {
    val bestOdds = GameRetriever.getOptimisedOdds

    val json = JObject(List(
      "oddsCollection" ->
        JArray(bestOdds.toList.map(_.toJson))
    ))

    Ok(JsonMethods.pretty(JsonMethods.render(json)))
  }
}