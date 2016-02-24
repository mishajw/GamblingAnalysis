package gamblinganalysis.util.db

import gamblinganalysis.{Bookie, Sport, User}
import gamblinganalysis.accounts.Account
import scalikejdbc._

import scala.math.BigDecimal.RoundingMode

object UserDBHandler extends BaseDBHandler {

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

  def insertAccount(account: Account): (Int, Int) = {
    val userId = insertUser(account.name)
    val bookieId = GameDetailsDBHandler.insertBookie(account.bookie)

    val optId = sql"""
         SELECT 1
         FROM account
         WHERE user_id = $userId
         AND bookie_id = $bookieId
       """.map(_.int("id")).single.apply()

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

  def users: Seq[User] = {
    sql"""
        SELECT name FROM user
    """.map(r => User(r.string("name")))
      .list
      .apply()
  }

  def accounts: Seq[Account] = {
    sql"""
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
  }

  private def balanceToSql(bd: BigDecimal): Int = (bd.setScale(2, RoundingMode.DOWN) * 100).toInt
  private def balanceToBigDecimal(i: Int) = BigDecimal(i) / 100
}
