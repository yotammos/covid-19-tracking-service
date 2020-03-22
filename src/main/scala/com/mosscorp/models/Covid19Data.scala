package com.mosscorp.models

import java.time.LocalDate

case class Covid19Data(cases: Long, deaths: Long, recovered: Long, date: LocalDate)
