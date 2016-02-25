package gamblinganalysis

import gamblinganalysis.accounts.Account
import gamblinganalysis.analysis.{AggressiveSimulator, BuyingPlan, OddsOptimiser}
import gamblinganalysis.factory._
import gamblinganalysis.odds.Odd
import gamblinganalysis.retriever.GameRetriever
import gamblinganalysis.retriever.odds.{actors, OddsCheckerRetriever, SkybetRetriever}
import gamblinganalysis.util.db.{UserDBHandler, GeneralDBHandler, GameDetailsDBHandler}
import gamblinganalysis.util.exceptions.ParseException
import play.api.Logger

/**
  * Created by misha on 20/02/16.
  */
object Main {
  private val log = Logger(getClass)

  def main(args: Array[String]): Unit = {
//    GeneralDBHandler.reset()
//
//    val sport = SportFactory get "Football"
//    val game = GameFactory get (Set("Win", "Lose", "Draw"), sport)
//    val bookie = BookieFactory get "Bet 365"
//
//    val odd1 = new Odd(3, 1, "Win", game, bookie)
//    val odd2 = new Odd(3, 1, "Lose", game, bookie)
//
//    GameDetailsDBHandler.insertOdd(odd1)
//    GameDetailsDBHandler.insertOdd(odd2)
//    actors.start()
//    GameDetailsDBHandler.allGames.foreach(g => {
//      println(OddsOptimiser.optimise(GameDetailsDBHandler.oddsForGame(g)))
//    })

//    GeneralDBHandler.reset()
//    val user = User("Misha")
//    val bookie = Bookie("Bet 365")
//    val account = new Account(user, BigDecimal(10), bookie)
//
//    UserDBHandler.insertAccount(account)
//
//    println(UserDBHandler.accounts.mkString("\n"))

//    GameDetailsDBHandler.allGames.foreach({ g =>
//      val oc = GameDetailsDBHandler.oddsForGame(g)
//      val opt = OddsOptimiser.optimise(oc)
//      UserDBHandler.insertBuyingPlan(opt)
//    })

    val plans = AggressiveSimulator.runWithPrint(GameDetailsDBHandler.allOdds, UserDBHandler.accounts)

    println(plans.mkString("\n"))

  }
}
