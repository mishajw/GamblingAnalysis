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

  def run(): Seq[BuyingPlan] = {
    val allOdds: Seq[OddsCollection] = getAllOdds

    val acc = 10
    val money = acc * 10

    if (money / 10 >= acc) {
      val accountsCollection = generateAccounts(acc, money)
      val allPlans = run(accountsCollection, allOdds)
      val profits = allPlans.map(_.profit)

      log.info(s"Accounts: $acc")
      log.info(s"Money: $money")
      log.info(s"Profits: ${profits.mkString(", ")}")
      log.info(s"Total of ${profits.sum} across ${allPlans.size} arbs")
      log.info(s"Return of ${(profits.sum / money) * 100}%")

      allPlans
    } else Seq()
  }

  private def run(accountsCollection: AccountsCollection, odds: Seq[OddsCollection]): Seq[BuyingPlan] = {
    odds.flatMap(oc => {
      accountsCollection.mostProfitable(oc) match {
        case Some(bestPlan) =>
          val profit: BigDecimal = bestPlan.roi
          if (profit > minimumAmount) {
            bestPlan.pairs foreach { pair =>
              pair.account.get.amount -= pair.money.get
            }

            Some(bestPlan)
          } else {
            None
          }
        case None => None
      }
    })
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
