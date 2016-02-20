package gamblinganalysis

/**
  * Created by misha on 20/02/16.
  */
package object factory {

  object GameOutcomeFactory extends BasicFactory[GameOutcome, (String, Game)] {
    def newType(t: (String, Game)) = t match { case (o, g) => GameOutcome(o, g) }
  }

  object GameFactory extends BasicFactory[Game, Set[String]] {
    def newType(identifier: Set[String]) = Game(identifier)
  }

  object OwnerFactory extends BasicFactory[AccountOwner, String]{
    def newType(name: String) = AccountOwner(name)
  }

  object BookieFactory extends BasicFactory[Bookie, String] {
    def newType(name: String) = Bookie(name)
  }
}
