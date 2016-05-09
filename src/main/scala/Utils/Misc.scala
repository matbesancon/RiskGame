package Misc

import io.StdIn;

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
}