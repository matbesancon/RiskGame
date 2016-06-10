package Misc

import io.StdIn
import WorldMap._

import annotation.tailrec
import scala.text.DocNest;

object Colors extends Enumeration{
  type Colors = Value
  val Green,Red,Yellow,Blue,Purple,Orange = Value
}

object Actions extends Enumeration{
  type Actions = Value
  val Move,Attack,NoAction = Value
}

//abstract class Actions
//case class Move extends Actions
//case class Attack extends Actions
//case class NoAction extends Actions

//object Actions extends Enumeration{
//  type Actions = Value
//  val Move, Attack, Display = Value
//}

object RobustReader{

  def testInt(s:String):Option[Int] = {
    try Some(s.toInt) catch {
      case _: java.lang.NumberFormatException => None
    }
  }

  @tailrec def robustInt(max:Int): Int = {
    println("Enter a value between 1 and "+max.toString)
    val value: Option[Int] = testInt(StdIn.readLine())
    value match {
      case Some(x) => {
        if (x > max) {
          println("Value above maximum value of " + max)
          robustInt(max)
        }else if(x<1){
          println("Value above minimum value of 1")
          robustInt(max)
        }
        else x
      }
      case None => {
        println("Error, you did not enter a number")
        robustInt(max)
      }
      case _ => {
        sys.error("Error in string to int conversion")
        robustInt(max)
      }
    }
  }

  @tailrec def robustCountry(allowedCountries: List[Country]): Country = {
    println("Allowed countries are: ")
    allowedCountries foreach println
    println("Enter a country name")
    val input: String = StdIn.readLine()
    val selection = allowedCountries filter {_.name==input}
    selection match {
      case Nil => {
        println("Country not found, the value "+input+" is incorrect.")
        robustCountry(allowedCountries)
      }
      case list => {
        if (list.length>1) sys.error("Several countries correspond to a name")
        else list.head
      }
    }
  }

  @tailrec def robustAction(stringSit: String): Actions.Value = {
    println("Select an action")
    println("Move: m - attack: a - display current situation: s - do nothing: n")
    val input: String = StdIn.readLine()
    if(input.length==0){
      println("No action entered")
      robustAction(stringSit)
    }else if (input.head=='s'){
      println(stringSit)
      robustAction(stringSit)
    }else if (input.head=='m'){
      println("Move troops")
      Actions.Move
    }else if (input.head=='a'){
      println("Attack")
      Actions.Attack
    }else if (input.head=='n'){
      println("Nothing done")
      Actions.NoAction
    }else{
      println("Action entered not valid")
      robustAction(stringSit)
    }
  }

}

object rulesContainer{
  val initialTroops: Map[Int,Int] = Map(3->35,4->30,5->25,6->20)
  def computeInitialTroops(nPlayers: Int): Int = {
    initialTroops.get(nPlayers) match {
      case None => sys.error("Number of players not allowed (choose between 2 and 6)")
      case Some(nTroops) => nTroops
    }
  }
}
