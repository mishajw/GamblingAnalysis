package models

import models.util.db.{UserDBHandler, GeneralDBHandler}
import play.api.Logger

object Main {
  private val log = Logger(getClass)

  def main(args: Array[String]): Unit = {
    GeneralDBHandler.reset()

    UserDBHandler.insertAccount(Account(User("Misha"), BigDecimal(10), Bookie("Bet 365")))
    val accounts = UserDBHandler.accounts

    println(accounts.accounts.mkString("\n"))
  }
}
