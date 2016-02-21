package gamblinganalysis.util.db

import gamblinganalysis.{bookies, users}
import scalikejdbc._

object GeneralDBHandler extends BaseDBHandler {
  def fillWithDefault() = {
    bookies map (b =>
      sql"""
           INSERT INTO bookie(name) VALUES ($b)
         """.update.apply())

    users map (u =>
      sql"""
           INSERT INTO user(name) VALUES ($u)
        """.update.apply())
  }
}
