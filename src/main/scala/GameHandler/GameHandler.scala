/**
  * Created by mbesancon on 08.05.16.
  */
package GameHandling;
import WorldMap._
import Player.{DefenseTroops, _}
import Misc._;

object GameHandler{

  def playTurn(player: Player, situation:Situation): Situation ={
    println("Play turn for player " + player.name)
    return situation
  }
//  def initSituation(val players: List[Player]): Situation = {
//
//  }

  def playAll(players: List[Player],situation: Situation): Situation = {
    if(players.isEmpty) situation
    else {
      playAll(players.tail,playTurn(players.head,situation))
    }
  }

  def move(player: Player,origin:Country,destination:Country,situation: Situation): Situation = {
    if(WorldMap.neighborhood.contains(Set(origin,destination)==false)){
      println("Origin and destination are not neighbors")
      situation
    }else{
      println("How many troops do you want to move?")
      val nTroopsOrigin: Int = situation.troopMap.apply(origin).troops.number
      val nMoved:Int = RobustReader.robustInt(nTroopsOrigin-1)
      val restTroops: DefenseTroops = new DefenseTroops(nTroopsOrigin-nMoved,player,origin)
      return situation.updated(origin,new CountrySituation(restTroops,player))
    }
  }
}