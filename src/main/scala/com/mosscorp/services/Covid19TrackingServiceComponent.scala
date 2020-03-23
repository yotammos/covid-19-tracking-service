package com.mosscorp.services

import java.time.{LocalDate, LocalDateTime, Month}
import java.time.format.DateTimeFormatter
import java.util.Calendar

import com.mosscorp.context.ComponentProvider
import com.mosscorp.models.{CSVConstants, Country, CountryData, CountryTimeData, Covid19Data}

trait Covid19TrackingServiceComponent {

  def covid19TrackingService: Covid19TrackingService

  class Covid19TrackingService(context: ComponentProvider) {
    import Country._

    private val firstPossibleDate: LocalDate = LocalDate.of(2020, Month.FEBRUARY, 2)

    def getAllCountryData: Array[CountryData] = {
      val all = getCountryDataByDate()
        .groupBy(_.name)
        .values
        .map(
          countryData => countryData
            .foldLeft(CountryData(countryData.head.name, Covid19Data(0, 0, 0, countryData.head.data.date)))(_ + _)
        ).toArray
      val total = all.foldLeft(CountryData("Total", Covid19Data(0, 0, 0, all.head.data.date)))(_ + _)
      (total +: all).sortBy(_.data.cases * -1)
    }

    private def getCountryDataByDate(relevantDay: LocalDate = getLastRelevantDate): Array[CountryData] =
      context.johnsHopkinsClient.getCountryData(buildCsvUrl(relevantDay)).data.map { x =>
        for {
          name <- x get CSVConstants.COUNTRY_REGION
          cases <- x get CSVConstants.CONFIRMED
          deaths <- x get CSVConstants.DEATHS
          recovered <- x get CSVConstants.RECOVERED
          lastUpdate <- x get CSVConstants.LAST_UPDATE
        } yield CountryData(name, Covid19Data(cases.toLong, deaths.toLong, recovered.toLong, LocalDateTime.parse(lastUpdate).toLocalDate))
      }.flatMap(x => if (x.isDefined) Array(x.get) else Array.empty[CountryData])

    def getCompleteCountryInfo(country: Country, relevantDate: LocalDate = getLastRelevantDate): CountryData = {
      val data = getCountryDataByDate(relevantDate)
      data
        .filter(countryData => country.getNames.contains(countryData.name))
        .foldLeft(CountryData(country.toName, Covid19Data(0, 0, 0, data.head.data.date)))(_ + _)
    }

    def getCountryTimeData(country: Country): CountryTimeData =
      CountryTimeData(
        country.toName,
        getAllPossibleDates
          .map(date => getCompleteCountryInfo(country, date))
          .map(_.data)
      )

    private def getAllPossibleDates: Array[LocalDate] = {
      val numberOfDays: Long = getLastRelevantDate.toEpochDay - firstPossibleDate.toEpochDay
      (0 to numberOfDays.toInt).reverse.map(i => firstPossibleDate.plusDays(i)).toArray
    }

    private def getLastRelevantDate: LocalDate = {
      val calendar: Calendar = Calendar.getInstance()
      val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
      val today = LocalDate.now()
      if (hourOfDay > 20) today else today minusDays 1
    }

    private def buildCsvUrl(relevantDay: LocalDate = getLastRelevantDate): String = {
      val formattedDay = relevantDay.format(DateTimeFormatter ofPattern "MM-dd-YYYY")
      s"https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_daily_reports/$formattedDay.csv"
    }
  }
}
