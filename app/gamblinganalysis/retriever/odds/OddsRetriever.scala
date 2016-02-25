package gamblinganalysis.retriever.odds

import gamblinganalysis.retriever.odds.actors.WorkerRetriever
import gamblinganalysis.{Game, Sport}
import gamblinganalysis.odds.OddsCollection
import gamblinganalysis.retriever.Retriever

/**
  * Created by misha on 23/02/16.
  */
trait OddsRetriever extends Retriever {
  def retrieve(worker: WorkerRetriever): OddsCollection
}
