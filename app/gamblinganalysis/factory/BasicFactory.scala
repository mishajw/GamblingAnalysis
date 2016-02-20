package gamblinganalysis.factory

/**
  * Created by misha on 20/02/16.
  */
trait BasicFactory[T] {
  /**
    * Existing types
    */
  protected val existing = new scala.collection.mutable.HashMap[String, T]

  /**
    * Get the object for a string
    * @param name string to lookup with
    * @return the object
    */
  def get(name: String): T = {
    if (!(existing contains name))
      existing(name) = newType(name)

    existing(name)
  }

  /**
    * Create a new type
    * @param name what to make it with
    * @return the new type
    */
  protected def newType(name: String): T
}
