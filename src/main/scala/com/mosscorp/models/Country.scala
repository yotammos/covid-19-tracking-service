package com.mosscorp.models

import CSVConstants._

abstract class Country(names: Array[String]) {
  def getNames: Array[String] = names
}
case object UnitedStates extends Country(Array(US_NAME))
case object China extends Country(Array(CHINA_NAME, CHINA_OTHER_NAME))
case object Israel extends Country(Array(ISRAEL_NAME))
case object Iran extends Country(Array(IRAN_NAME))
case object Italy extends Country(Array(ITALY_NAME))
case object Germany extends Country(Array(GERMANY_NAME))
case object Spain extends Country(Array(SPAIN_NAME))
case object France extends Country(Array(FRANCE_NAME))
case object SouthKorea extends Country(Array(SOUTH_KOREA_NAME, SOUTH_KOREA_OTHER_NAME))
case object Switzerland extends Country(Array(SWITZERLAND_NAME))
case object UnitedKingdom extends Country(Array(UK_NAME, UK_OTHER_NAME))
case object Netherlands extends Country(Array(NETHERLANDS_NAME))
case object UnknownCountry extends Country(Array(UNKNOWN_NAME))

object Country {
  def nameToCountry(name: String): Country = name match {
    case US_NAME => UnitedStates
    case CHINA_NAME | CHINA_OTHER_NAME => China
    case ISRAEL_NAME => Israel
    case IRAN_NAME => Iran
    case ITALY_NAME => Italy
    case GERMANY_NAME => Germany
    case SPAIN_NAME => Spain
    case FRANCE_NAME => France
    case SOUTH_KOREA_NAME | SOUTH_KOREA_OTHER_NAME => SouthKorea
    case SWITZERLAND_NAME => Switzerland
    case UK_NAME | UK_OTHER_NAME => UnitedKingdom
    case UNKNOWN_NAME | _ => UnknownCountry
  }

  def countryToName(country: Country): String =
    country.getNames.headOption
      .getOrElse(throw new Exception("country not found"))

  implicit class CountryOps(country: Country) {
    def toName: String = Country.countryToName(country)
  }

  implicit class CountryNameOps(name: String) {
    def toCountry: Country = Country.nameToCountry(name)
  }
}
