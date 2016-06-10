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

}
