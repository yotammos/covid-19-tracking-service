package com.mosscorp.models

case class CSVData(colNames: Array[String], data: Array[Map[String, String]]) {
  override def toString: String =
    data.map(_.map(x => s"${x._1} -> ${x._2}").mkString(",")).mkString("\n")
}
