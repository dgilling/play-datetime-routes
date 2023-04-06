package com.github.dgilling.datetimeroutes

import org.scalatest.{Matchers, WordSpecLike}
import org.joda.time.{DateTime, DateTimeZone}
import org.joda.time.format.DateTimeFormat

class DateTimeRoutesTest extends WordSpecLike with Matchers {

  def parseDateTime(date: String): DateTime = {
    JodaPeriodConverter.parseFromPeriod(date, JodaDateTimeRoutes.formats , DateTimeZone.UTC, 1)
  }

  "DateTimeRoutes" should {
    "parse period correctly" in {

      val expectedDateTime: DateTime = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ").parseDateTime("2023-04-01T00:00:00.000Z")

      // Date in format yyyy-MM-dd'T'HH:mm:ssZ
      val dateWithSecondsAndTz: DateTime = parseDateTime("2023-04-01T00:00:00Z")
      dateWithSecondsAndTz shouldBe expectedDateTime

      // Date in format yyyy-MM-dd'T'HH:mm:ss
      val dateWithSecondsWithoutTz: DateTime = parseDateTime("2023-04-01T00:00:00")
      dateWithSecondsWithoutTz shouldBe expectedDateTime

      // Date in format yyyy-MM-dd'T'HH:mm:ss.000Z
      val dateWithMsWithPrecision3AndTz: DateTime = parseDateTime("2023-04-01T00:00:00.000Z")
      dateWithMsWithPrecision3AndTz shouldBe expectedDateTime

      // Date in format yyyy-MM-dd'T'HH:mm:ss.000
      val dateWithMsWithPrecision3WithoutTz: DateTime = parseDateTime("2023-04-01T00:00:00.000")
      dateWithMsWithPrecision3WithoutTz shouldBe expectedDateTime

      // Date in format yyyy-MM-dd'T'HH:mm:ss.00Z
      val dateWithMsWithPrecision2AndTz: DateTime = parseDateTime("2023-04-01T00:00:00.00Z")
      dateWithMsWithPrecision2AndTz shouldBe expectedDateTime

      // Date in format yyyy-MM-dd'T'HH:mm:ss.00
      val dateWithMsWithPrecision2WithoutTz: DateTime = parseDateTime("2023-04-01T00:00:00.00")
      dateWithMsWithPrecision2WithoutTz shouldBe expectedDateTime

      // Date in format yyyy-MM-dd'T'HH:mm:ss.000000Z
      val dateWithMicroSecAndTz: DateTime = parseDateTime("2023-04-01T00:00:00.000000Z")
      dateWithMicroSecAndTz shouldBe expectedDateTime

      // Date in format yyyy-MM-dd'T'HH:mm:ss.000000
      val dateWithMicroSecWithoutTz: DateTime = parseDateTime("2023-04-01T00:00:00.000000")
      dateWithMicroSecWithoutTz shouldBe expectedDateTime

      // Date in format yyyy-MM-dd'T'HH:mm:ss.000000000
      val dateWithNanoSecWithoutTz: DateTime = parseDateTime("2023-04-01T00:00:00.000000000")
      dateWithNanoSecWithoutTz shouldBe expectedDateTime

      // Date in format yyyy-MM-dd'T'HH:mm:ss.000000000Z
      val dateWithNanoSecAndTz: DateTime = parseDateTime("2023-04-01T00:00:00.000000000Z")
      dateWithNanoSecAndTz shouldBe expectedDateTime
    }
  }
}
