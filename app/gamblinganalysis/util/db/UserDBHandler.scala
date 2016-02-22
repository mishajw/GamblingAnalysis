package gamblinganalysis.util.db

import gamblinganalysis.AccountOwner
import scalikejdbc._

object UserDBHandler extends BaseDBHandler {
  def users: Seq[AccountOwner] = {
    sql"""
        SELECT * FROM user
    """.map(_.toMap)
      .list
      .apply()
      .map(r => AccountOwner(r("name").toString))
      .toSeq
  }


}
