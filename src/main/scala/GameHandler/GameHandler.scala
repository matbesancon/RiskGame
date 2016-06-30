/**
  * Created by mbesancon on 08.05.16.
  */
package GameHandling;
import scala.language.postfixOps
import util.Random.nextInt
import WorldMap.{Country, _}
import Player.{AttackTroops, DefenseTroops, _}
import Misc._

import util.Try
import scala.annotation.tailrec;

object GameHandler {

  @tailrec def init(players: Vector[Player], countries: List[Country],
                    troopMap: List[(Country, CountrySituation)] = Nil): Situation = {
    // affecting countries to players by recursively building
    if (countries.isEmpty) new Situation(troopMap.toMap)
    else init(players.tail :+ players.head, countries.tail, troopMap :+(countries.head,
      new CountrySituation(new DefenseTroops(1, players.head, countries.head), players.head)))
  }

  // TODO: Add rules in separate file 
  val placementMap: Map[Int, Int] = Map(
    3 -> 35,
    4 -> 30,
    5 -> 25,
    6 -> 20)

  @tailrec def placeTroops(situation: Situation, player: Player, nTroops: Int): Situation = {
    if (nTroops<0) sys.error("Negative number of troops to place in placeTroops")
    if (nTroops == 0) situation
    // if (nTroops > 0)
    else {
      val playerCountry: Map[Country, CountrySituation] = situation.troopMap.filter(
        (pair: (Country, CountrySituation)) => pair._2.player == player
      )
      println("Player " + player.name + " still has " + nTroops + " to place.")
      println("You can place troops on the following countries:")
      playerCountry.foreach { pair => println(pair._1.toString) }
      val selectedCountry: Country = RobustReader.robustCountry(playerCountry.map(pair => pair._1).toList)
      println("How many troops do you wish to place on that country.")
      val wishedTroops: Int = RobustReader.robustInt(nTroops)
      val actualTroops = situation.troopMap.apply(selectedCountry).troops
      val newSit: Situation = situation.updated(
        selectedCountry, new CountrySituation(
          new DefenseTroops(actualTroops.number + wishedTroops, player, selectedCountry), player
        )
      )
      placeTroops(newSit, player, nTroops - wishedTroops)
    }
  }

  @tailrec def initPlace(situation: Situation, players: List[Player], nTroops: Option[Int] = None): Situation = {
    // place additional troops at the beginning
    players match {
      case Nil => situation
      case _ => {
        nTroops match {
          case None => initPlace(situation, players,
            Some(rulesContainer.computeInitialTroops(players.length))
          )
          case Some(nTroops) => initPlace(placeTroops(situation, players.head, nTroops), players.tail, Some(nTroops))
        }
      }
    }
  }

  // TODO: Finish function playTurn
  @tailrec def playTurn(player: Player, situation: Situation): Situation = {
    // if action, recurse, else finish turn
    println("Play turn for player " + player.name)
    println(situation.toString)
    println("--------------")
    val newTroops: Int = situation.troopsPlace(player)
    val placedSit: Situation = placeTroops(situation,player,newTroops)
    val action: Actions.Value = RobustReader.robustAction(placedSit.toString)
    if(action==Actions.Move) playTurn(player,wrapperMove(player,placedSit))
    else if(action==Actions.Attack) playTurn(player,wrapperAttack(player,placedSit))
    else if(action==Actions.NoAction) placedSit
    else sys.error("Error, unexpected action value")
  }

  @tailrec def playAll(players: List[Player], situation: Situation): Situation = {
    if (players.isEmpty) situation
    else {
      playAll(players.tail, playTurn(players.head, situation))
    }
  }

  def wrapperMove(player: Player, situation: Situation): Situation = {
    println("Moving troops...")
    println("Select a country to move from.")
    val playerCountries = situation.filterPlayerCountries(player)
    val origin = RobustReader.robustCountry(playerCountries)
    println("Select a country to move to.")
    val destination = RobustReader.robustCountry(
      playerCountries.toSet.intersect(WorldMap.findNeighbors(origin).toSet).toList
    )
    move(player,origin,destination,situation)
  }

  def move(player: Player, origin: Country, destination: Country, situation: Situation): Situation = {
    val nTroopsOrigin: Int = situation.troopMap.apply(origin).troops.number
    if (nTroopsOrigin <= 1) {
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

  @tailrec def fight(target: Country,attackTroops: AttackTroops,situation: Situation): Situation = {
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
            situation.troopMap.apply(target).player, target),
          situation.troopMap.apply(target).player
        )
      )
      fight(target, new AttackTroops(
        attackTroops.number-outcome.head,attackTroops.player,attackTroops.origin),newCountSit)
    }
  }

  def wrapperAttack(player: Player, situation: Situation): Situation = {
    println("Attacking another country...")
    println("Select a country to attack from.")
    val playerCountries = situation.filterPlayerCountries(player)
    val origin = RobustReader.robustCountry(playerCountries)
    val targets: List[Country] = WorldMap.findNeighbors(origin).filter(!playerCountries.contains(_))
    if (targets.isEmpty) {
      println("No attackable country connected.")
      situation
    }else {
      println("Select a country to attack.")
      val target = RobustReader.robustCountry(targets)
      attack(origin,target,situation,player)
    }
  }

  def attack(origin:Country,target:Country,situation: Situation,attacker: Player):Situation ={
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
      val restTroops: DefenseTroops = new DefenseTroops(
        nTroopsOrigin-nMoved,
        attacker, origin
      )
      fight(target,new AttackTroops(nMoved,attacker,origin),
        situation.updated(origin, new CountrySituation(restTroops, attacker)))
    }
  }
}
