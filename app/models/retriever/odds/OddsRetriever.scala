package models.retriever.odds

import models.retriever.odds.actors.WorkerRetriever
import models.{Game, Sport}
import models.odds.OddsCollection
import models.retriever.Retriever

/**
  * Created by misha on 23/02/16.
  */
trait OddsRetriever extends Retriever {
  def retrieve(worker: WorkerRetriever): OddsCollection
}
