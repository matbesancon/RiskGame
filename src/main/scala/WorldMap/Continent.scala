/**
  * Created by mbesancon on 06.05.16.
  */
package WorldMap
class Continent(val name: String, val bonus:Int) {
  override def toString: String = "Continent "+ name + ", giving a bonus of "+ bonus.toString
  // def printCountries: Unit = countries foreach {println(_.toString)}
}
