package gamblinganalysis.factory

import gamblinganalysis.AccountOwner

/**
  * Created by misha on 20/02/16.
  */
object OwnerFactory extends BasicFactory[AccountOwner]{
  def newType(name: String) = AccountOwner(name)
}
