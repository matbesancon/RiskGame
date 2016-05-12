import WorldMap.{Country, _}
import Player.{DefenseTroops, _}
import Misc._;
import GameHandling._;

object RiskGame {
  def main(args: Array[String]) {
    println("Let the game begin.")
    val testcont: Continent = new Continent("testcont",3)
    val testc: Country = new Country("country",testcont,3)
    val testplay:Player = new Player("john",Colors.Green)
    val player2:Player = new Player("jack",Colors.Blue)
    val country2 = new Country("count2",testcont,2)
    if(country2==testc) sys.error("equal countries")
    val troops: DefenseTroops = new DefenseTroops(3,testplay,country2)

    val iniSit: Situation = GameHandler.init(Vector(testplay,player2),WorldMap.countries)
    iniSit.troopMap foreach println
//    println(troops.number)
//    println(troops.player)
//    println(troops.country)
  }
}
