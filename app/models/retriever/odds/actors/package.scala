package models.retriever.odds

import akka.actor.{Props, ActorSystem}

/**
  * Created by misha on 24/02/16.
  */
package object actors {
  // Actor communications
  sealed trait ActorCommunication
  case class StartWorkers(work: Seq[OddsRetriever]) extends ActorCommunication
  case class DoneScraping(work: OddsRetriever) extends ActorCommunication
  case class ScrapingWork(work: OddsRetriever) extends ActorCommunication
  case class NoMoreWork() extends ActorCommunication

  private val amountOfWorkers = 10

  private val retrievers = Seq(
    SkybetRetriever
  )

  def start() = {
    val system = ActorSystem("ScrapingSystem")

    val master = system.actorOf(Props(new MasterRetriever(amountOfWorkers)))
    master ! StartWorkers(retrievers)
  }
}
