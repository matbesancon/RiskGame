/**
  * Created by mbesancon on 08.05.16.
  */

package GameHandling;

import Player._;
import WorldMap._;
import Misc._;

// situation: Country -> (troops present, authorized to move, player)
class CountrySituation(val troops: DefenseTroops,val player: Player){
  override def toString = "player: " + player.name+ ", troops " + troops.number
}

class Situation(val troopMap: Map[Country,CountrySituation]){
  def updated(country: Country,countrySituation: CountrySituation): Situation = new Situation(
    troopMap.updated(country,countrySituation)
  )
  override def toString = "Current situation: " + troopMap.map(
    (pair:(Country,CountrySituation))=> "\n Country "+pair._1.name + ", "+ pair._2.toString
  ).toString

  def troopsPlace(player: Player): Int = {
    val playerCountries = troopMap.filter(
      (pair:(Country,CountrySituation))=>
      pair._2.player==player)
      .map(_._1).toList
    val continentBonus: Int = WorldMap.countriesByContinent.filter(
      (pair: (Continent,List[Country])) => pair._2.forall(
        playerCountries.contains(_)
      )
    ).map(_._1.bonus).sum
    println("Continent bonus: "+continentBonus)
    val totBonus: Int = playerCountries.length/3 + continentBonus
    Math.max(3,totBonus)
  }
}
