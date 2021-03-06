package models.retriever.odds.actors

import akka.actor.{Props, ActorRef, Actor}
import models.retriever.odds.OddsRetriever
import play.api.Logger

import scala.collection.mutable

class MasterRetriever(workerAmount: Int) extends Actor {

  private val log = Logger(getClass)

  private val workQueue = new mutable.Queue[OddsRetriever]
  private val workerRouter = context.actorOf(
    Props[WorkerRetriever].withRouter(akka.routing.RoundRobinPool(workerAmount)), name = "workerRouter")

  def receive = {
    case StartWorkers(work) =>
      log.debug(s"Got work of length ${work.size}")
      work foreach (workQueue.enqueue(_))

      for (i <- 0 to workerAmount)
        workerRouter ! nextWork
      log.debug(s"Sent out work")
    case DoneScraping(work) =>
      log.debug(s"Worker done working with: $work")
      workQueue enqueue work
      workerRouter ! nextWork
  }

  def nextWork = workQueue.nonEmpty match {
    case true => ScrapingWork(workQueue.dequeue())
    case false => NoMoreWork()
  }
}
