package models.util.db

import scalikejdbc._
import models.Bookie
import scalikejdbc.{AutoSession, ConnectionPool, GlobalSettings, LoggingSQLAndTimeSettings}

/**
  * Created by misha on 21/02/16.
  */
class BaseDBHandler {
  /**
    * Initialise the database
    */
  Class.forName("org.sqlite.JDBC")
  ConnectionPool.singleton("jdbc:sqlite:/home/misha/Dropbox/scala/GamblingAnalysis/gambling.db", null, null)

  /**
    * Stop mad logging
    */
  GlobalSettings.loggingSQLAndTime = LoggingSQLAndTimeSettings(
    enabled = false
  )

  /**
    * The database session
    */
  implicit val session = AutoSession

  /**
    * Location of SQL files
    */
  protected val sqlFolder: String = "res/sql"
}
