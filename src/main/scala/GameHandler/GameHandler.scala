/**
  * Created by mbesancon on 08.05.16.
  */
package GameHandling;
import scala.language.postfixOps
import util.Random.nextInt
import WorldMap._
import Player.{AttackTroops, DefenseTroops, _}
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

  def throwDice(defDice:List[Int],attDice:List[Int]): List[Int] = {
    // returns losses of defense and of attack
    (attDice,defDice) match {
      case (Nil,_) => List(0,0)
      case (_,Nil) => List(0,0)
      case _ => {
        if(attDice.sorted.head>defDice.sorted.head){
          (List(0,1),throwDice(defDice.sorted.tail,attDice.sorted.tail)).zipped.map(_+_)
        }else{
          (List(1,0),throwDice(defDice.sorted.tail,attDice.sorted.tail)).zipped.map(_+_)
        }
      }
    }
  }

  def fight(target: Country,attackTroops: AttackTroops,situation: Situation): Situation = {
    if(attackTroops.number<=0){
      println("Attacker "+ attackTroops.player.name+" defeated.")
      situation
    }else if(situation.troopMap.apply(target).troops.number<=0){
      println("Defender "+ situation.troopMap.apply(target).player.name+" defeated.")
      println("Country "+target.name+" taken by "+attackTroops.player.name)
      situation.updated(target,new CountrySituation(
        new DefenseTroops(attackTroops.number,attackTroops.player,target),
        attackTroops.player))
    }else {
      // throw dice to determine outcome
      val outcome: List[Int] = throwDice(
        (for(d<-1 to situation.troopMap.apply(target).troops.number) yield nextInt(6)+1).toList,
        (for(d<-1 to attackTroops.number) yield nextInt(6)+1).toList
      )
      val newCountSit: Situation = situation.updated(target,
        new CountrySituation(
          new DefenseTroops(
            situation.troopMap.apply(target).troops.number-outcome.tail.head,
            situation.troopMap.apply(target).player, target
          ),situation.troopMap.apply(target).player
        )
      )

      fight(target, new AttackTroops(
        attackTroops.number-outcome.head,attackTroops.player,attackTroops.origin),newCountSit)
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