package controllers

import gamblinganalysis.retriever.GameRetriever
import org.json4s._
import org.json4s.jackson.JsonMethods
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