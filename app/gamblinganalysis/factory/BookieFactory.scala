package gamblinganalysis.factory

import gamblinganalysis.Bookie

/**
  * Created by misha on 20/02/16.
  */
object BookieFactory extends BasicFactory[Bookie] {
  def newType(name: String) = Bookie(name)
}
