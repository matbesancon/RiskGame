// TODO: Replace manual object with automatic input and list of countries + list of connections 

/**
  * Created by mbesancon on 06.05.16.
  */
package WorldMap

object WorldMap {
  // val connections: List[Set(Country,Country)] = List()
  // continent definitions
  val northAmerica: Continent = new Continent("North America",5)
  val europe: Continent = new Continent("Europe",5)
  val southAmerica: Continent = new Continent("South America",2)
  val africa: Continent = new Continent("Africa",3)
  val asia: Continent = new Continent("Asia",7)
  val oceania: Continent = new Continent("Oceania",2)

  // continents List
  val continents: List[Continent] = List(
    northAmerica,europe,southAmerica,africa,asia,oceania
  )

  // country definitions
  // europe
  val greatBrit = new Country("Great Britain",europe,1)
  val iceland = new Country("Iceland",europe,2)
  val northEurope = new Country("Northern Europe",europe,3)
  val scandinavia = new Country("Scandinavia",europe,4)
  val southEurope = new Country("Southern Europe",europe,5)
  val ukraine = new Country("Ukraine",europe,6)
  val westEurope = new Country("Western Europe",europe,7)

  val countries: List[Country] = List(
    greatBrit,iceland,northEurope,scandinavia,
    southEurope,ukraine,westEurope)

  val countriesByContinent: List[(Continent,List[Country])] = continents map{
    (cont: Continent) => (cont, countries filter {_.continent==cont})
  }
  // africa
//  val congo = new Country("Congo",africa,1)
//  val eastAfrica = new Country("East Africa",africa,2)
//  val egypt = new Country("Egypt",africa,3)
//  val madagascar = new Country("Madagascar",africa,4)
//  val northAfrica = new Country("North Africa",africa,5)
//  val southAfrica = new Country("South Africa",africa,6)

  val neighborhood: List[Set[Country]] = List(
    Set(greatBrit,iceland),
    Set(greatBrit,iceland),
    Set(greatBrit,westEurope),
    Set(greatBrit,scandinavia),
    Set(greatBrit,northEurope),
    Set(iceland,scandinavia),
    Set(scandinavia,ukraine),
    Set(northEurope,southEurope),
    Set(northEurope,ukraine),
    Set(northEurope,westEurope),
    Set(northEurope,scandinavia),
    Set(southEurope,ukraine),
    Set(southEurope,westEurope)
  )
}
