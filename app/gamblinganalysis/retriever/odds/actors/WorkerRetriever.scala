package gamblinganalysis.retriever.odds.actors

import akka.actor.Actor
import gamblinganalysis.util.db.GameDetailsDBHandler
import play.api.Logger

/**
  * Created by misha on 24/02/16.
  */
class WorkerRetriever extends Actor {

  private val log = Logger(getClass)

  def receive = {
    case ScrapingWork(work) =>
      log.debug(s"Got work: $work")
      val oddsCollection = work.retrieve()

      log.debug(s"Inserting ${oddsCollection.odds.size} into database")
      oddsCollection.odds.foreach(GameDetailsDBHandler.insertOdd)

      log.debug(s"Done with work: $work")
      sender() ! DoneScraping(work)
    case NoMoreWork() =>
      log.debug("Told no more work")
  }
}
