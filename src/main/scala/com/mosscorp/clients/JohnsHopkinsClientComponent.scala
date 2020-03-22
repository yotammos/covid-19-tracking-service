package com.mosscorp.clients

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import com.mosscorp.models.{CSVData, Country}

trait JohnsHopkinsClientComponent {

  def johnsHopkinsClient: JohnsHopkinsClient

  class JohnsHopkinsClient {

    private val cols = Array("Province/State", "Country/Region", "Confirmed", "Deaths", "Recovered", "Last Update")

    def getCountryData(csvUrl: String): CSVData = HttpClient.fetchCsvData(csvUrl, cols)
  }
}
