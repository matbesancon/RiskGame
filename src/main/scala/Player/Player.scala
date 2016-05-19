package Player;

import WorldMap._;
import Misc.Colors;
import GameHandling._;

// TODOdone: fix enumeration for colors
// val color:Colors.Value

// TODO: Delete circular dependencies player troops situation

class Player(val name:String,val color: Colors.Value,val inGame:Boolean=true){

//  def attack(country: Country,situation:Situation,troops:AttackTroops) = {
//    val attackDice: Int = Math.max(troops.number,3)
//    val defenseDice: Int = Math.max(situation.troopMap.apply(country).number,2)
//  }
}

abstract class Troops(val number: Int, val player: Player){
}

class AttackTroops(number: Int,player: Player,val origin: Country) extends Troops(number, player){
}

class DefenseTroops(number: Int,player: Player,val country: Country) extends Troops(number, player){
}