package gamblinganalysis.analysis

import gamblinganalysis.accounts.{Account, AccountsCollection}
import gamblinganalysis.factory.{BookieFactory, OwnerFactory}
import gamblinganalysis.odds.OddsCollection
import gamblinganalysis.retriever.GameRetriever
import gamblinganalysis.retriever.odds.OddsCheckerRetriever
import gamblinganalysis.util.exceptions.ParseException
import play.api.Logger

import scala.collection.mutable.ListBuffer
import scala.math.BigDecimal
import scala.util.Random

object AggressiveSimulator {

  private val log = Logger(getClass)

  val bookies = Seq(
    "Totesport", "Winner", "Stan James", "Paddy Power", "BetBright", "Matchbook",
    "10Bet", "Betfair", "Betdaq", "Boylesports", "Marathon Bet", "Unibet", "Ladbrokes",
    "888sport", "Bet 365", "Sportingbet", "Bwin", "William Hill", "Bet Victor",
    "Netbet UK", "Betway", "Betfair Sportsbook", "32Red Bet", "Sky Bet", "Coral",
    "Betfred")

  val names = Seq(
    "Misha", "Hannah", "Jodie", "Mona", "Zoe", "Harry", "Joe", "Ali"
  )

  def run(): Unit = {
    val allOdds = getAllOdds

    for (acc <- 10 to 10 ; money <- 100 to 500 by 25) {
      if (money / 10 > acc) {
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
  }

  private def run(accountsCollection: AccountsCollection, odds: Seq[Seq[OddsCollection]]): Seq[BigDecimal] = {
    val allProfits = ListBuffer[BigDecimal]()

    odds.foreach(o => {
      accountsCollection.mostProfitable(o) match {
        case Some(bestPlan) =>
          val profit: BigDecimal = bestPlan.profit
          if (profit > 0) {
            allProfits += profit

            bestPlan.getAmounts.foreach { case (acc, amount) =>
              acc.amount -= amount
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
    }).sortBy(-OddsOptimiser.optimise(_).getInvestmentReturn)
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
            OwnerFactory get Random.shuffle(names).head,
            money / amount,
            BookieFactory get b)
        })

    new AccountsCollection(accounts)
  }
}
