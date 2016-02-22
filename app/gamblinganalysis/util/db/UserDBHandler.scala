package gamblinganalysis.util.db

import gamblinganalysis.User
import scalikejdbc._

object UserDBHandler extends BaseDBHandler {
  def users: Seq[User] = {
    sql"""
        SELECT * FROM user
    """.map(_.toMap)
      .list
      .apply()
      .map(r => User(r("name").toString))
      .toSeq
  }


}
