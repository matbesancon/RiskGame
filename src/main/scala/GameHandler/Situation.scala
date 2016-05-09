/**
  * Created by mbesancon on 08.05.16.
  */

package GameHandling;

import Player._;
import WorldMap._;
import Misc._;

// situation: Country -> (troops present, authorized to move, player)
class CountrySituation(val troops: DefenseTroops,val player: Player){

}

class Situation(val troopMap: Map[Country,CountrySituation]){
  def updated(country: Country,countrySituation: CountrySituation): Situation = {
    val newTroop = troopMap.updated(country,countrySituation)
    return new Situation(newTroop)
  }
}