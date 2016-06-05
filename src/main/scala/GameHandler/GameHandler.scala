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

  @tailrec def placeTroops(player: Player, situation: Situation, nTroops: Int): Situation = {
    if (nTroops <= 0)
      situation
    else {
      println("Still " + nTroops + " to place.")
      println("Current map: ")
      situation.troopMap foreach println
      println("Enter a country name where you want to place troops.")
      val inputCount: String = io.StdIn.readLine
      if (!WorldMap.countries.map(_.name).contains(inputCount)) {
        println("Country not in the map! (Typo maybe?)")
        placeTroops(player, situation, nTroops)
      } else {
        val country = WorldMap.countries.filter(_.name == inputCount).head
        if (situation.troopMap.apply(country).player != player) {
          println("The country belongs to " + situation.troopMap.apply(country).player.name + " and not to " + player.name)
          println("Choose another one")
          placeTroops(player, situation, nTroops)
        } else {
          println("How many troops on country " + country.name + "?")
          val inpNum: Try[Int] = Try(io.StdIn.readInt)
          if (inpNum.isFailure) {
            println("Incorrect input, integer expected")
            placeTroops(player, situation, nTroops)
          } else {
            val newSit: Situation = new Situation(situation.troopMap.updated(country, new CountrySituation(
              new DefenseTroops(situation.troopMap.apply(country).troops.number + inpNum.get, player, country), player)))
            placeTroops(player, newSit, nTroops - inpNum.get)
          }
        }
      }
    }
  }

  @tailrec def placeTroops(situation: Situation, player: Player, nTroops: Int): Situation = {
    if (nTroops == 0) situation
    else if (nTroops > 0) {
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
    else sys.error("Negative number of troops to place in placeTroops")
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
  def playTurn(player: Player, situation: Situation): Situation = {
    println("Play turn for player " + player.name)
    situation
  }

  @tailrec def playAll(players: List[Player], situation: Situation): Situation = {
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
            situation.troopMap.apply(target).player, target),
          situation.troopMap.apply(target).player
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

      fight(target,new AttackTroops(nMoved,attacker,origin),
        situation.updated(origin, new CountrySituation(restTroops, attacker)))
    }
  }
}