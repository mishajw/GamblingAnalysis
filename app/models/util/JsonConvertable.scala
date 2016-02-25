package models.util

import org.json4s.JsonAST.JObject

/**
  * Created by misha on 23/02/16.
  */
trait JsonConvertable {
  /**
    * Convert to JSON
    * @return JSON obejct
    */
  def toJson: JObject
}
