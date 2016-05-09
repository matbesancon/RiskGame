/**
  * Created by mbesancon on 06.05.16.
  */
package WorldMap
class Country(val name: String, val continent: Continent, val id: Int) {
  override def toString: String = "Country "+name
  def equals(that:Country):Boolean = this.name == that.name
}
