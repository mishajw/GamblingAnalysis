package gamblinganalysis.util.db

import scalikejdbc._

object UserDBHandler extends BaseDBHandler {
  def test() = {
    println("Starting test")

    val results: List[Map[String, Any]] = sql"""
        SELECT * FROM user
    """.map(_.toMap).list.apply()

    println(results)
  }


}
