package com.mosscorp.clients

import com.mosscorp.models.CSVData
import com.twitter.finagle.{Http, Service}
import com.twitter.finagle.http.{Method, Request, Response}
import com.twitter.util.Await

object HttpClient {
  def fetchPageSource(url: String): String = {
    val startTime = System.currentTimeMillis()
    val source = scala.io.Source fromURL url
    val result = source.mkString
    source.close()
    println(s"total time in milliseconds: ${System.currentTimeMillis() - startTime}")
    result
  }

  def anotherFetchPageSource(host: String, path: String, port: Int = 443): String = {
    val client: Service[Request, Response] = Http.newService(host + ":" + port)
    val request = Request(Method.Get, path)
    request.host = host

    val twitter: Service[Request, Response] = Http.client.newService("twitter.com:443")
    println(Await.result(twitter(Request("/"))).getContentString())

    Await.result(
      client(request) foreach { response: Response =>
        println("status code = " + response.statusCode)
      }
    )
    ""
  }

  private def handleCommas(line: String): String = {
    if (line.count(_ == '\"') % 2 != 0) {
      throw new Exception("Uneven number of quotes")
    } else {
      def loop(line: String, quoteIndices: List[Array[Int]]): String =
        if (quoteIndices.isEmpty) line
        else {
          val first = quoteIndices.head.head
          val second = quoteIndices.head(1)
          loop(
            line.slice(0, first) +
              line
              .slice(first, second)
              .replaceAll(",", "-")
                .replaceAll(" ", "") +
              line.slice(second, line.length),
            quoteIndices.tail
          )
        }

      loop(line, line.indices.toArray.filter(i => line(i) == '\"').grouped(2).toList)
        .replaceAll("\"", "")
    }
  }

  def fetchCsvData[T](url: String, colNames: Array[String], emptyColNotation: String = "N/A"): CSVData = {
    val bufferedSource = scala.io.Source fromURL url
    val lineList = bufferedSource
      .getLines
      .map(handleCommas)
      .toList
      .map {
        _.split(",")
          .map(_.trim)
      }

    val colNameMap = colNames.map(colName => (lineList.head indexOf colName, colName))
        .map(x => if (x._1 == -1 && x._2 == "Province/State") (0, x._2) else x)
    val colNameIndices = colNameMap.map(_._1)
    if (colNameIndices contains -1) {
      throw new Exception("column name not found")
    }

    val data = lineList.tail.toArray.map { line =>
      val x = line.indices flatMap { i =>
        if (colNameIndices contains i) Map[String, String](
          colNameMap.find(_._1 == i).get._2 -> (
            if (line(i).isEmpty) emptyColNotation
            else line(i)
            )
        )
        else Map.empty[String, String]
      }
      x.toMap
    }

    bufferedSource.close

    CSVData(
      colNames,
      data
    )
  }
}
