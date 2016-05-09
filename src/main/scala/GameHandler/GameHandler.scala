/**
  * Created by mbesancon on 08.05.16.
  */
package GameHandling;
import WorldMap._
import Player.{DefenseTroops, _}
import Misc._;

object GameHandler {

  def playTurn(player: Player, situation: Situation): Situation = {
    println("Play turn for player " + player.name)
    return situation
  }

  //  def initSituation(val players: List[Player]): Situation = {
  //
  //  }

  def playAll(players: List[Player], situation: Situation): Situation = {
    if (players.isEmpty) situation
    else {
      playAll(players.tail, playTurn(players.head, situation))
    }
  }

  def move(player: Player, origin: Country, destination: Country, situation: Situation): Situation = {
    val nTroopsOrigin: Int = situation.troopMap.apply(origin).troops.number
    if (WorldMap.neighborhood.contains(Set(origin, destination) == false)) {
      println("Origin and destination are not neighbors")
      situation
    } else if (nTroopsOrigin <= 1) {
      println("Not enough troops to move")
      situation
    } else {
      println("How many troops do you want to move?")
      val nTroopsDest: Int = situation.troopMap.apply(destination).troops.number
      val nMoved: Int = RobustReader.robustInt(nTroopsOrigin - 1)
      val restTroops: DefenseTroops = new DefenseTroops(nTroopsOrigin - nMoved, player, origin)
      val newTroops: DefenseTroops = new DefenseTroops(nMoved + nTroopsDest, player, destination)
      situation.updated(origin, new CountrySituation(restTroops, player)).updated(
        destination, new CountrySituation(newTroops, player))
    }
  }

  def fight(defenseTroops: DefenseTroops,attackTroops: AttackTroops,situation: Situation): Situation = {
    if(attackTroops.number<=0){
      println("Attacker "+ attackTroops.player.name+" defeated.")
      situation
    }else if(defenseTroops.number<=0){
      println("Defender "+ defenseTroops.player.name+" defeated.")
      println("Country "+defenseTroops.country.name+" taken by "+attackTroops.player.name)
      situation.updated(defenseTroops.country,new CountrySituation(
        new DefenseTroops(attackTroops.number,attackTroops.player,defenseTroops.country),
        attackTroops.player))
    }else {
//      println("Defender "+ defenseTroops.country+" defeated.")
      situation
    }
  }

  def attack(attacker:Player,defender:Player,origin:Country,target:Country,situation: Situation):Situation ={
    val nTroopsOrigin: Int = situation.troopMap.apply(origin).troops.number
    if (WorldMap.neighborhood.contains(Set(origin, target) == false)) {
      println("Origin and destination are not neighbors")
      situation
    } else if (nTroopsOrigin <= 1) {
      println("Not enough troops to move")
      situation
    } else {
      println("With how many troops do you want to attack?")
      val nTroopsDef: Int = situation.troopMap.apply(target).troops.number
      val nMoved: Int = RobustReader.robustInt(nTroopsOrigin - 1)
      val restTroops: DefenseTroops = new DefenseTroops(nTroopsOrigin - nMoved, attacker, origin)

      situation.updated(origin, new CountrySituation(restTroops, attacker))
    }
  }
}