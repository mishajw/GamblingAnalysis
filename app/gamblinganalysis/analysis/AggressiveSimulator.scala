package gamblinganalysis.analysis

import gamblinganalysis.accounts.{Account, AccountsCollection}
import gamblinganalysis.factory.{BookieFactory, UserFactory}
import gamblinganalysis.odds.OddsCollection
import gamblinganalysis.retriever.GameRetriever
import gamblinganalysis.retriever.odds.OddsCheckerRetriever
import gamblinganalysis.util.exceptions.ParseException
import gamblinganalysis.{bookies, users}
import play.api.Logger

import scala.collection.mutable.ListBuffer
import scala.math.BigDecimal
import scala.util.Random

object AggressiveSimulator {

  private val log = Logger(getClass)

  private val minimumAmount = BigDecimal(1)

  def run(): Unit = {
    val allOdds: Seq[OddsCollection] = getAllOdds

    val acc = 10
    val money = acc * 10

    if (money / 10 >= acc) {
      val accountsCollection = generateAccounts(acc, money)
      val allProfits = run(accountsCollection, allOdds)

      log.info(s"Accounts: $acc")
      log.info(s"Money: $money")
      log.info(s"Profits: ${allProfits.mkString(", ")}")
      log.info(s"Total of ${allProfits.sum} across ${allProfits.size} arbs")
      log.info(s"Return of ${(allProfits.sum / money) * 100}%")

      println
    }
  }

  private def run(accountsCollection: AccountsCollection, odds: Seq[OddsCollection]): Seq[BigDecimal] = {
    val allProfits = ListBuffer[BigDecimal]()

    odds.foreach(oc => {
      accountsCollection.mostProfitable(oc) match {
        case Some(bestPlan) =>
          val profit: BigDecimal = bestPlan.roi
          if (profit > minimumAmount) {
            allProfits += profit

            bestPlan.pairs foreach { pair =>
              pair.account.get.amount -= pair.money.get
            }
          }
        case None =>
      }
    })

    allProfits.toSeq
  }

  private def getAllOdds = {
    GameRetriever.retrieve.flatMap(g => {
      try {
        Some(OddsCheckerRetriever.getOdds(g))
      } catch {
        case e: Exception => None
        case e: ParseException => None
      }
    }).sortBy(-OddsOptimiser.optimise(_).roi)
  }

  private def generateAccounts(amount: Int, money: BigDecimal): AccountsCollection = {
    if (money / 10 < amount) {
      throw new IllegalArgumentException("Can't deposit less than £10 in each account.")
    }

    val accounts =
      Stream.continually(bookies)
        .flatten
        .take(amount)
        .map(b => {
          new Account(
            UserFactory get Random.shuffle(users).head,
            money / amount,
            BookieFactory get b)
        })

    new AccountsCollection(accounts)
  }
}
