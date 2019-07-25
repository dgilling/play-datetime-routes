package com.github.dgilling.datetimeroutes

import org.joda.time.format.DateTimeFormat
import play.api.mvc.{PathBindable, QueryStringBindable}
import org.joda.time.{DateTimeZone, DateTime}
import scala.util.matching.Regex._

/**
  * Created by derric on 5/12/16.
  */
trait JodaDateTimeRoutes { self: JodaFormat =>

  val format: String

  implicit object queryStringPeriodBinder extends QueryStringBindable.Parsing[DateTime](
    dateString =>  JodaPeriodConverter.parsePeriod(dateString, format),
    _.toString(format),
    (key: String, e: Exception) => "Cannot parse parameter %s as org.joda.time.DateTime: %s".format(key, e.getMessage)
  )

  implicit object pathPeriodBinder extends PathBindable.Parsing[DateTime](
    dateString =>  JodaPeriodConverter.parsePeriod(dateString, format),
    _.toString(format),
    (key: String, e: Exception) => "Cannot parse parameter %s as org.joda.time.DateTime: %s".format(key, e.getMessage)
  )
}

object JodaDateTimeRoutes extends JodaDateTimeRoutes
  with DefaultJodaFormat

object JodaPeriodConverter {

  val SecondsRegex = """([+-]?)(\d+)[Ss]""".r
  val MinutesRegex = """([+-]?)(\d+)[m]""".r
  val HoursRegex = """([+-]?)(\d+)[Hh]""".r
  val DaysRegex = """([+-]?)(\d+)[Dd]""".r
  val WeeksRegex = """([+-]?)(\d+)[Ww]""".r
  val MonthsRegex = """([+-]?)(\d+)[M]""".r

  def parsePeriod(dateTime: String, format: String): DateTime = {
    dateTime match {
      case "now" => DateTime.now(DateTimeZone.UTC)
      case SecondsRegex(sign: String, value: String) => DateTime.now.plusSeconds((sign + value).toInt)
      case MinutesRegex(sign, value) => DateTime.now.plusMinutes((sign + value).toInt)
      case HoursRegex(sign, value) => DateTime.now.plusHours((sign + value).toInt)
      case DaysRegex(sign, value) => DateTime.now.plusDays((sign + value).toInt)
      case WeeksRegex(sign, value) => DateTime.now.plusWeeks((sign + value).toInt)
      case MonthsRegex(sign, value) => DateTime.now.plusMonths((sign + value).toInt)
      case _ => DateTimeFormat.forPattern(format).parseDateTime(dateTime)
    }
  }
}