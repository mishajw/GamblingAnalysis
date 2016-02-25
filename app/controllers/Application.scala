package controllers

import models.analysis.{OddsOptimiser, AggressiveSimulator}
import models.retriever.GameRetriever
import org.json4s._
import org.json4s.jackson.JsonMethods
import play.api.mvc._

object Application extends Controller {

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def bestOdds = Action {
    val bestOdds = OddsOptimiser.getMostOptimised

    val json = JObject(List(
      "bestOdds" ->
        JArray(bestOdds.toList.map(_.toJson))
    ))

    Ok(formatJson(json))
  }

  def aggressivePlan = Action {
    val plans = AggressiveSimulator.run()

    val json = JObject(List(
      "aggressivePlan" ->
        JArray(plans.map(_.toJson).toList)
    ))

    Ok(formatJson(json))
  }

  def formatJson(json: JObject) = JsonMethods.pretty(JsonMethods.render(json))
}