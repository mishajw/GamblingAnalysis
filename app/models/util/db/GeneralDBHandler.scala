package models.util.db

import models.{bookies, users}
import scalikejdbc._

import scala.io.Source

object GeneralDBHandler extends BaseDBHandler {

  def reset() = {
    dropAllTables()
    createAllTables()
  }

  private def dropAllTables() = {
    val f = Source.fromFile(sqlFolder + "/drop_tables.sql")
    val rawStatements = f.mkString.split("\n")

    rawStatements foreach { s =>
      SQL(s).update().apply()
    }

    f.close()
  }

  private def createAllTables() = {
    val f = Source.fromFile(sqlFolder + "/create_tables.sql")
    val rawStatements = f.mkString.split("\n\n")

    rawStatements foreach { s =>
      SQL(s).update().apply()
    }

    f.close()
  }

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
