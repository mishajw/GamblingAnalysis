package models

/**
  * Created by misha on 20/02/16.
  */
package object factory {
  object GameFactory extends BasicFactory[Game, (Set[String], Sport)] {
    def newType(t: (Set[String], Sport)) = t match { case (o, s) => Game(o, s) }
  }

  object UserFactory extends BasicFactory[User, String] {
    def newType(name: String) = User(name)
  }

  object BookieFactory extends BasicFactory[Bookie, String] {
    def newType(name: String) = Bookie(name)
  }

  object SportFactory extends BasicFactory[Sport, String] {
    def newType(name: String) = Sport(name)
  }
}
