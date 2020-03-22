package com.mosscorp

import com.mosscorp.context.ComponentProvider
import com.mosscorp.models.{Country, CountryData, CountryTimeData}
import com.twitter.app.Flag
import com.twitter.finagle.{Http, Service}
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finagle.http.filter.Cors
import com.twitter.server.TwitterServer
import com.twitter.util.{Await, Future}
import io.finch.syntax._
import io.finch._
import io.finch.circe._
import io.circe.generic.auto._

object CoronaTrackingApplicationService extends TwitterServer {
  import Country._

  private val serverPort: Int = 8080
  private val port: Flag[Int] = flag("port", serverPort, "TP port for HTTP server")
  private val context = new ComponentProvider

  private val policy: Cors.Policy = Cors.Policy(
    allowsOrigin = _ => Some("*"),
    allowsMethods = _ => Some(Seq("GET", "POST")),
    allowsHeaders = _ => Some(Seq("Accept", "Content-Type"))
  )

  def counts: Endpoint[CountryData] = get("counts" :: path[String]: Endpoint[String]) {
    country: String => Future.value(context.covid19TrackingService.getCompleteCountryInfo(country.toCountry)) map Ok
  }

  def allCounts: Endpoint[Array[CountryData]] = get("counts" :: "all") {
    Future.value(context.covid19TrackingService.getAllStats) map Ok
  }

  def countryTimeData: Endpoint[CountryTimeData] = get("time" :: path[String]) {
    country: String => Future.value(context.covid19TrackingService.getCountryTimeData(country.toCountry)) map Ok
  }

  private val api = (countryTimeData :+: allCounts :+: counts).handle {
    case e: Exception =>
      println("Error in COVID-19 Tracking Service, error = " + e.getMessage)
      InternalServerError(e)
  }

  private val serviceWithCors: Service[Request, Response] = new Cors.HttpFilter(policy).andThen(api.toServiceAs[Application.Json])

  def main(): Unit = {
    println(s"Serving the application on port ${port()}")

    val server =
      Http.server
        .withStatsReceiver(statsReceiver)
        .serve(s":${port()}", serviceWithCors)
    closeOnExit(server)

    Await ready adminHttpServer
  }
}
