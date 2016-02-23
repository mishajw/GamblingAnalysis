package gamblinganalysis.util

import org.json4s.JsonAST.JObject

/**
  * Created by misha on 23/02/16.
  */
trait JsonConvertable {
  def toJson: JObject
}
