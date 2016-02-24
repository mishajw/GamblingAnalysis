package gamblinganalysis.retriever.odds.actors

import akka.actor.Actor
import play.api.Logger

/**
  * Created by misha on 24/02/16.
  */
class WorkerRetriever extends Actor {

  private val log = Logger(getClass)

  def receive = {
    case ScrapingWork(work) =>
      log.debug(s"Got work: $work")
      work.retrieve()
      log.debug(s"Done with work: $work")
      sender() ! DoneScraping(work)
  }
}
