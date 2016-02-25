package controllers

import models.OddPair
import models.analysis.{OddsOptimiser, AggressiveSimulator}
import models.retriever.GameRetriever
import models.util.JsonConverter
import org.json4s._
import org.json4s.jackson.JsonMethods
import org.json4s.native.JsonParser
import play.api.mvc._

object Application extends Controller {

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def bestOdds = Action {
    val bestOdds = OddsOptimiser.getMostOptimised

    val json = JObject(List(
      "bestOdds" ->
        JArray(bestOdds.toList.map(JsonConverter.fromBuyingPlan))
    ))

    Ok(formatJson(json))
  }

  def aggressivePlan = Action {
    val plans = AggressiveSimulator.run()

    val json = JObject(List(
      "aggressivePlan" ->
        JArray(plans.map(JsonConverter.fromBuyingPlan).toList)
    ))

    Ok(formatJson(json))
  }

  def carryOutPlan(jsonString: String) = Action {
    val json = JsonParser.parse(jsonString)

//    val odds = for (
//      JObject(obj) <- json;
//      JField("odds", odds) <- obj;
//      JObject(odd) <- odds;
//      JField("numerator", JInt(numerator)) <- odd;
//      JField("denominator", JInt(denominator)) <- odd;
//      JField("bookie", JString(bookie)) <- odd;
//      JField("outcome", JString(outcome)) <- odd
//    ) yield OddPair()

    Ok("")
  }

  def formatJson(json: JObject) = JsonMethods.pretty(JsonMethods.render(json))
}