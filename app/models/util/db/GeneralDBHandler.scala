package models.util.db

import models.{bookies, users}
import scalikejdbc._

import scala.io.Source

object GeneralDBHandler extends BaseDBHandler {

  /**
    * Reset the database to have nothing in it
    */
  def reset() = {
    dropAllTables()
    createAllTables()
  }

  /**
    * Drop all tables
    */
  private def dropAllTables() = {
    val f = Source.fromFile(sqlFolder + "/drop_tables.sql")
    val rawStatements = f.mkString.split("\n")

    rawStatements foreach { s =>
      SQL(s).update().apply()
    }

    f.close()
  }

  /**
    * Create all tables
    */
  private def createAllTables() = {
    val f = Source.fromFile(sqlFolder + "/create_tables.sql")
    val rawStatements = f.mkString.split("\n\n")

    rawStatements foreach { s =>
      SQL(s).update().apply()
    }

    f.close()
  }
}
