package models.util.db

import scalikejdbc._
import models.Bookie
import scalikejdbc.{AutoSession, ConnectionPool, GlobalSettings, LoggingSQLAndTimeSettings}

/**
  * Created by misha on 21/02/16.
  */
class BaseDBHandler {
  Class.forName("org.sqlite.JDBC")
  ConnectionPool.singleton("jdbc:sqlite:/home/misha/Dropbox/scala/GamblingAnalysis/gambling.db", null, null)

  GlobalSettings.loggingSQLAndTime = LoggingSQLAndTimeSettings(
    enabled = false
  )

  // ad-hoc session provider on the REPL
  implicit val session = AutoSession

  protected val sqlFolder: String = "res/sql"
}