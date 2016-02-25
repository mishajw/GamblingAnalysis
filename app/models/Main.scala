package models

import models.util.db.{UserDBHandler, DBInitializer}
import play.api.Logger

object Main {
  private val log = Logger(getClass)

  def main(args: Array[String]): Unit = {
    DBInitializer.fillWithJson()
    val accounts = UserDBHandler.accounts

    log.info(accounts.accounts.mkString("\n"))
  }
}
