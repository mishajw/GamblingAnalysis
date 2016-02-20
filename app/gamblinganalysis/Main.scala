package gamblinganalysis

import gamblinganalysis.accounts.{AccountsCollection, Account, BuyingPlan}
import gamblinganalysis.analysis.OddsOptimiser
import gamblinganalysis.factory.{GameFactory, GameOutcomeFactory, BookieFactory, OwnerFactory}
import gamblinganalysis.odds.Odd
import gamblinganalysis.retriever.GameRetriever
import gamblinganalysis.retriever.odds.{OddsCheckerRetriever, SkybetRetriever}
import gamblinganalysis.util.exceptions.ParseException
import play.api.Logger

import scala.util.Random

/**
  * Created by misha on 20/02/16.
  */
object Main {
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

  def main(args: Array[String]) {
    runAccountsCollection()
  }

  def runOddsChecker() = {
    log.info("Starting OddsChecker")

    val odds = OddsCheckerRetriever.getOdds("http://www.oddschecker.com/tennis/atp-marseille/peter-gojowczyk-v-kenny-de-schepper/winner")
    val optimum = OddsOptimiser.optimise(odds)

    optimum.printSafeBet()
  }

  def runGameRetriever() = {
    log.info("Starting GameRetriever")

    GameRetriever.retrieve.flatMap(g => {
      try {
        val odds = OddsCheckerRetriever.getOdds(g)
        val optimum = OddsOptimiser.optimise(odds)
        Some(optimum, optimum.getInvestmentReturn)
      } catch {
        case e: Exception => None
        case e: ParseException => None
      }
    })
      .sortBy { case (odds, ir) => -ir }
      .map { case (odds, ir) => odds }
      .foreach(o => {
        o.printSafeBet()
        println()
      })
  }

  def runSkybet() = {
    log.info("Starting Skybet")

    val results = SkybetRetriever.run()
    results.foreach(r => log.info(r.toString))
  }

  def runBuyingPlan() = {
    log.info("Starting buying plan")

    val game = GameFactory get Set("Win", "Draw", "Lose")
    val bet365: Bookie = BookieFactory get "bet365"

    val plan = new BuyingPlan(Seq(
      (
        new Account(OwnerFactory get "Misha", BigDecimal(7), bet365),
        new Odd(3, 1, GameOutcomeFactory get ("Win", game), bet365)
      ),
      (
        new Account(OwnerFactory get "Hannah", BigDecimal(10), bet365),
        new Odd(2, 1, GameOutcomeFactory get ("Draw", game), bet365)
      ),
      (
        new Account(OwnerFactory get "Jodie", BigDecimal(10), bet365),
        new Odd(2, 1, GameOutcomeFactory get ("Lose", game), bet365)
      )
    ))

    log.info(s"Profit: ${plan.profit}, Limiting account: ${plan.getLimitingAccount}")
  }

  def runAccountsCollection() = {
    log.info("Starting accounts collection")

    val accounts = bookies.map(b => {
      new Account(
        OwnerFactory get Random.shuffle(names).head,
        BigDecimal(Random.nextDouble() * 10 + 10),
        BookieFactory get b)
    })

    val allOdds = GameRetriever.retrieve.flatMap(g => {
      try {
        Some(OddsCheckerRetriever.getOdds(g))
      } catch {
        case e: Exception => None
        case e: ParseException => None
      }
    })
      .sortBy(-OddsOptimiser.optimise(_).getInvestmentReturn)

    val accountsCollection = new AccountsCollection(accounts)

    allOdds.foreach(o => {
      accountsCollection.mostProfitable(o) match {
        case Some(bestPlan) =>
          log.info(s"Buying plan with a profit of £${bestPlan.profit}:\n$bestPlan")
        case None =>
          log.info("Couldn't find a combination")
      }
    })
  }
}
