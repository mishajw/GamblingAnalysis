package gamblinganalysis

import gamblinganalysis.retriever.Retriever

/**
  * Created by misha on 23/02/16.
  */
package object odds {
  // Actor communications
  sealed trait ActorCommunication
  case class DoneScraping() extends ActorCommunication
  case class ScrapingWork(retriever: Retriever) extends ActorCommunication
}
