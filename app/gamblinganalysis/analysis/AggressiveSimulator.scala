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
    val accountsCollection = generateAccounts()
    val allProfits = run(accountsCollection, allOdds)

    accountsCollection.accounts.foreach(a => log.info(a.toString))

    log.info(s"Profits: ${allProfits.mkString(", ")}")
    log.info(s"Total of ${allProfits.sum} across ${allProfits.size} arbs")
  }

  private def run(accountsCollection: AccountsCollection, odds: Seq[Seq[OddsCollection]]): Seq[BigDecimal] = {
    var allProfits = ListBuffer[BigDecimal]()

    odds.foreach(o => {
      accountsCollection.mostProfitable(o) match {
        case Some(bestPlan) =>
          val profit: BigDecimal = bestPlan.profit
          log.info(s"Can make: $profit")

          if (profit > 0) {
            allProfits += profit

            bestPlan.getAmounts.foreach { case (acc, amount) =>
              acc.amount -= amount
            }
          }
        case None =>
          log.info("Couldn't find a combination")
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

  private def generateAccounts(amount: Int = 10, money: BigDecimal = 200): AccountsCollection = {
    if (money / 10 < amount) {
      throw new IllegalArgumentException("Can't deposit less than £10 in each account.")
    }

    val accounts =
      Stream.continually(Random.shuffle(bookies))
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
