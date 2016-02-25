package models.analysis

import models.accounts.AccountsCollection
import models.odds.OddsCollection
import models.util.db.{UserDBHandler, GameDetailsDBHandler}
import play.api.Logger

import scala.math.BigDecimal

object AggressiveSimulator {

  private val log = Logger(getClass)

  private val minimumAmount = BigDecimal(-100)

  def run(): Seq[BuyingPlan] =
    run(GameDetailsDBHandler.allOdds, UserDBHandler.accounts)

  def runWithPrint(allOdds: Seq[OddsCollection], allAccounts: AccountsCollection): Seq[BuyingPlan] = {
    val allPlans = run(allOdds, allAccounts)
    val profits = allPlans.map(_.profit)

    log.info(s"Accounts: ${allAccounts.accounts.size}")
    log.info(s"Money: ${allAccounts.totalMoney}")
    if (profits.nonEmpty) {
      log.info(s"Profits: ${profits.mkString(", ")}")
      log.info(s"Total of ${profits.sum} across ${allPlans.size} arbs")
      log.info(s"Return of ${(profits.sum / allAccounts.totalMoney) * 100}%")
    } else {
      log.info("No profitable plan found.")
    }

    allPlans
  }

  def run(odds: Seq[OddsCollection], accountsCollection: AccountsCollection): Seq[BuyingPlan] = {
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
}
