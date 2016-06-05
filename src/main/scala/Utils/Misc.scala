package Misc

import io.StdIn;
import WorldMap._;

object Colors extends Enumeration{
  type Colors = Value
  val Green,Red,Yellow,Blue,Purple,Orange = Value
}

object RobustReader{

  def testInt(s:String):Option[Int] = {
    try Some(s.toInt) catch {
      case _: java.lang.NumberFormatException => None
    }
  }

  def robustInt(max:Int): Int = {
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

  def robustCountry(allowedCountries: List[Country]): Country = {
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
