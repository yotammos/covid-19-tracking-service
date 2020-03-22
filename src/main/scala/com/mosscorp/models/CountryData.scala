package com.mosscorp.models

case class CountryData(name: String, data: Covid19Data) {
  def +(countryData: CountryData): CountryData =
    CountryData(
      if (name != countryData.name) "Total" else name,
      Covid19Data(
        data.cases + countryData.data.cases,
        data.deaths + countryData.data.deaths,
        data.recovered + countryData.data.recovered,
        data.date
      )
    )
}
