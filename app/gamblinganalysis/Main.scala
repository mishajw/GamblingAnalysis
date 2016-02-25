package gamblinganalysis

import gamblinganalysis.util.db.GeneralDBHandler
import play.api.Logger

object Main {
  private val log = Logger(getClass)

  def main(args: Array[String]): Unit = {
    GeneralDBHandler.reset()
  }
}
