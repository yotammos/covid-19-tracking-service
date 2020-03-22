package com.mosscorp.models

abstract class Country
case object US extends Country
case object China extends Country
case object Israel extends Country
case object Iran extends Country
case object Italy extends Country
case object Germany extends Country
case object Spain extends Country
case object UnknownCountry extends Country

object Country {
  private val US_NAME = "US"
  private val CHINA_NAME = "China"
  private val ISRAEL_NAME = "Israel"
  private val IRAN_NAME = "Iran"
  private val ITALY_NAME = "Italy"
  private val GERMANY_NAME = "Germany"
  private val SPAIN_NAME = "Spain"
  private val UNKNOWN_NAME = "Unknown"

  def nameToCountry(name: String): Country = name match {
    case US_NAME => US
    case CHINA_NAME => China
    case ISRAEL_NAME => Israel
    case IRAN_NAME => Iran
    case ITALY_NAME => Italy
    case GERMANY_NAME => Germany
    case SPAIN_NAME => Spain
    case UNKNOWN_NAME | _ => UnknownCountry
  }

  def countryToName(country: Country): String = country match {
    case US => US_NAME
    case China => CHINA_NAME
    case Israel => ISRAEL_NAME
    case Iran => IRAN_NAME
    case Italy => ITALY_NAME
    case Germany => GERMANY_NAME
    case Spain => SPAIN_NAME
    case UnknownCountry | _ => UNKNOWN_NAME
  }

  implicit class CountryOps(country: Country) {
    def toName: String = Country.countryToName(country)
  }

  implicit class CountryNameOps(name: String) {
    def toCountry: Country = Country.nameToCountry(name)
  }

  val allCountries = Array(
    US_NAME,
    CHINA_NAME,
    ISRAEL_NAME,
    IRAN_NAME,
    ITALY_NAME,
    GERMANY_NAME,
    SPAIN_NAME
  )
}
