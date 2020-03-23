package com.mosscorp

import com.mosscorp.models.UnitedStates
import io.finch.Input
import org.scalatest.{FlatSpec, Matchers}

class CoronaTrackingApplicationServiceSpec extends FlatSpec with Matchers {

  import CoronaTrackingApplicationService._
  import com.mosscorp.models.Country._

  behavior of "the counts endpoint"

  it should "get counts for country" in {
    counts(Input.get("/counts/US")).awaitValueUnsafe().get.name shouldBe UnitedStates.toName
  }

  behavior of "the all counts endpoint"

  it should "get counts for all countries" in {
    allCounts(Input.get("/counts/all")).awaitValueUnsafe().get.head.name shouldBe "Total"
  }
}
