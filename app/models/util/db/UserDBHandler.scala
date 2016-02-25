package models.util.db

import models.accounts.AccountsCollection
import models.analysis.BuyingPlan
import models.{Account, Bookie, OddPair, User}
import play.api.Logger
import scalikejdbc._

import scala.math.BigDecimal.RoundingMode

object UserDBHandler extends BaseDBHandler {

  private val log = Logger(getClass)

  /**
    * @param user the user to insert
    * @return the ID of the user
    */
  def insertUser(user: User): Int = {
    val optId =
      sql"""
           SELECT id FROM user WHERE name = ${user.name}
         """.map(_.int("id")).single.apply()

    optId match {
      case Some(id) => id
      case x =>
        sql"""
             INSERT INTO user(name) VALUES (${user.name})
           """.updateAndReturnGeneratedKey().apply().toInt
    }
  }

  /**
    * @param account the account to insert
    * @return the ID of the account
    */
  def insertAccount(account: Account): (Int, Int) = {
    val userId = insertUser(account.name)
    val bookieId = GameDetailsDBHandler.insertBookie(account.bookie)

    val optId = sql"""
         SELECT user_id, bookie_id
         FROM account
         WHERE user_id = $userId
         AND bookie_id = $bookieId
       """.map(r => (r.int("user_id"), r.int("bookie_id"))).single.apply()

    if (optId.isEmpty) {
        sql"""
             INSERT INTO account (user_id, bookie_id)
             VALUES ($userId, $bookieId)
           """.update.apply()

        sql"""
           INSERT INTO account_transaction (user_id, bookie_id, amount)
           VALUES($userId, $bookieId, ${balanceToSql(account.amount)})
         """.update.apply()
    }

    (userId, bookieId)
  }

  /**
    * @param plan the plan to insert
    */
  def insertBuyingPlan(plan: BuyingPlan): Unit = {
    if (!plan.complete) {
      log.error(s"Tried to add incomplete plan: $plan")
      return
    }

    val arbId = sql"""
         INSERT INTO arbitration DEFAULT VALUES
       """.updateAndReturnGeneratedKey().apply().toInt

    val gameId = GameDetailsDBHandler.insertGame(plan.game)

    plan.pairs foreach { case OddPair(odd, Some(money), Some(account)) =>
      val outcomeId =
        sql"""
             SELECT id
             FROM game_outcome
             WHERE game_id = $gameId
             AND outcome = ${odd.outcome}
           """.map(_.int("id")).single.apply()

      val userId = insertUser(account.name)

      val bookieId = GameDetailsDBHandler.insertBookie(account.bookie)

      val transactionId =
        sql"""
             INSERT INTO account_transaction(user_id, bookie_id, amount)
             VALUES ($userId, $bookieId, ${balanceToSql(money)})
           """.update.apply()

      sql"""
           INSERT INTO arbitration_transactions(outcome, bookie_id, transaction_id, arbitration_id)
           VALUES ($outcomeId, $bookieId, $transactionId, $arbId)
         """.update.apply()
    }
  }

  /**
    * @return the list of users from the database
    */
  def users: Seq[User] = {
    sql"""
        SELECT name FROM user
    """.map(r => User(r.string("name")))
      .list
      .apply()
  }

  /**
    * @return the list of accounts from the database
    */
  def accounts: AccountsCollection = {
    val accounts = sql"""
        SELECT U.name AS user, B.name AS bookie, BAL.amount AS amount
        FROM user U, account A, bookie B, (
          SELECT SUM(amount) AS amount, user_id, bookie_id
          FROM account_transaction
          GROUP BY user_id, bookie_id
        ) AS BAL
        WHERE A.user_id = U.id
        AND A.bookie_id = B.id
        AND A.user_id = BAL.user_id
        AND A.bookie_id = BAL.bookie_id
    """.map(r => new Account(User(r.string("user")), balanceToBigDecimal(r.int("amount")), Bookie(r.string("bookie"))))
      .list
      .apply()

    new AccountsCollection(accounts)
  }

  /**
    * @param bd big decimal in pound form
    * @return an integer in pence form
    */
  private def balanceToSql(bd: BigDecimal): Int = (bd.setScale(2, RoundingMode.DOWN) * 100).toInt

  /**
    * @param i an integer in pence form
    * @return a big decimal in pound form
    */
  private def balanceToBigDecimal(i: Int) = BigDecimal(i) / 100
}
