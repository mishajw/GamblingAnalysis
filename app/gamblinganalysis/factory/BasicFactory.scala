package gamblinganalysis.factory

/**
  * Created by misha on 20/02/16.
  */
trait BasicFactory[T] {
  protected val existing = new scala.collection.mutable.HashMap[String, T]

  def get(name: String): T = {
    if (!(existing contains name))
      existing(name) = newType(name)

    existing(name)
  }

  abstract def newType(name: String): T
}
