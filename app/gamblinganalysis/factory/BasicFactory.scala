package gamblinganalysis.factory

/**
  * Created by misha on 20/02/16.
  */
trait BasicFactory[T, U] {
  /**
    * Existing types
    */
  protected val existing = new scala.collection.mutable.HashMap[U, T]

  /**
    * Get the object for a string
    * @param identifier string to lookup with
    * @return the object
    */
  def get(identifier: U): T = {
    if (!(existing contains identifier))
      existing(identifier) = newType(identifier)

    existing(identifier)
  }

  /**
    * Create a new type
    * @param identifier what to make it with
    * @return the new type
    */
  protected def newType(identifier: U): T
}
