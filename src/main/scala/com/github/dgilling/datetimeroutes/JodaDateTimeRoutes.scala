package com.github.dgilling.datetimeroutes

import org.joda.time.format.DateTimeFormat
import play.api.mvc.{PathBindable, QueryStringBindable}
import org.joda.time.{DateTime, DateTimeZone}

import scala.annotation.tailrec
import scala.util.{Success, Try}
import scala.util.matching.Regex._

/**
  * Created by derric on 5/12/16.
  */

class DateTimeParsing[A](parse: (String, String, DateTimeZone, Int) => A, serialize: A => String, error: (String, Exception) => String)
  extends QueryStringBindable[A] {

  def bind(key: String, params: Map[String, Seq[String]]) = params.get(key).flatMap(_.headOption).map { dateString =>
    try {
      val timeZone = DateTimeZone.forID(params.get("time_zone").flatMap(_.headOption).getOrElse("UTC"))
      val weekStartsOn = params.get("week_starts_on").flatMap(_.headOption).map{_.toInt}.getOrElse(1)
      Right(parse(key, dateString, timeZone, weekStartsOn))
    } catch {
      case e: Exception => Left(error(key, e))
    }
  }
  def unbind(key: String, value: A) = key + "=" + serialize(value)
}

trait JodaDateTimeRoutes { self: JodaFormats =>

  val formats: Seq[String]

  implicit object queryStringDateRangeBinder extends DateTimeParsing[DateTime](
    (key, dateString, timeZone, weekStartsOn) =>  if (key.toLowerCase == "from") {
      JodaPeriodConverter.parseFromPeriod(dateString, formats, timeZone, weekStartsOn)
    } else {
      JodaPeriodConverter.parseToPeriod(dateString, formats, timeZone, weekStartsOn)
    },
    _.toString(formats.head),
    (key: String, e: Exception) => "Cannot parse parameter %s as com.github.dgilling.datetimeroutes.DateRange: %s".format(key, e.getMessage)
  )

  implicit object pathPeriodBinder extends PathBindable.Parsing[DateTime](
    dateString =>  JodaPeriodConverter.parsePeriod(dateString, formats, DateTimeZone.UTC, 1),
    _.toString(formats.head),
    (key: String, e: Exception) => "Cannot parse parameter %s as org.joda.time.DateTime: %s".format(key, e.getMessage)
  )
}

object JodaDateTimeRoutes extends JodaDateTimeRoutes
  with DefaultJodaFormats

object JodaPeriodConverter {

  val SecondsRegex = """([+-]?)(\d+)[Ss]""".r
  val MinutesRegex = """([+-]?)(\d+)[m]""".r
  val HoursRegex = """([+-]?)(\d+)[Hh]""".r
  val DaysRegex = """([+-]?)(\d+)[Dd]""".r
  val WeeksRegex = """([+-]?)(\d+)[Ww]""".r
  val MonthsRegex = """([+-]?)(\d+)[M]""".r

  trait Interval
  object Seconds extends Interval
  object Minutes extends Interval
  object Hourly extends Interval
  object Daily extends Interval
  object Weekly extends Interval
  object Monthly extends Interval

  def parseFromPeriod(fromString: String, formats: Seq[String], timeZone: DateTimeZone, weekStartsOn: Int): DateTime = {
    // if timerange is relative, then from will be reset to start of the interval
    val fromResetFcn = Option((interval: Interval, dateTime: DateTime) => {
      Option(interval).collect {
        case Seconds => (dateTime: DateTime) => dateTime.withMillisOfSecond(0)
        case Minutes => (dateTime: DateTime) => dateTime.withSecondOfMinute(0).withMillisOfSecond(0)
        case Hourly => (dateTime: DateTime) => dateTime.withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0)
        case Daily => (dateTime: DateTime) => dateTime.withMillisOfDay(0)
        case Weekly => (dateTime: DateTime) => dateTime.withDayOfWeek(weekStartsOn).withMillisOfDay(0)
        case Monthly => (dateTime: DateTime) => dateTime.withDayOfMonth(1).withMillisOfDay(0)
      }.map(_(dateTime)).getOrElse(dateTime)
    })

    parsePeriod(fromString, formats, timeZone, weekStartsOn, fromResetFcn)
  }

  def parseToPeriod(fromString: String, formats: Seq[String], timeZone: DateTimeZone, weekStartsOn: Int): DateTime = {
    // if timerange is relative, then from will be reset to start of the interval
    val fromResetFcn = Option((interval: Interval, dateTime: DateTime) => {
      Option(interval).collect {
        case Seconds => (dateTime: DateTime) => dateTime.plusSeconds(1).withMillisOfSecond(0).minusMillis(1)
        case Minutes => (dateTime: DateTime) => dateTime.plusMinutes(1).withSecondOfMinute(0).withMillisOfSecond(0).minusMillis(1)
        case Hourly => (dateTime: DateTime) => dateTime.plusHours(1).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0).minusMillis(1)
        case Daily => (dateTime: DateTime) => dateTime.plusDays(1).withMillisOfDay(0).minusMillis(1)
        case Weekly => (dateTime: DateTime) => dateTime.plusWeeks(1).withDayOfWeek(weekStartsOn).withMillisOfDay(0).minusMillis(1)
        case Monthly => (dateTime: DateTime) => dateTime.plusMonths(1).withDayOfMonth(1).withMillisOfDay(0).minusMillis(1)
      }.map(_(dateTime)).getOrElse(dateTime)
    })

    parsePeriod(fromString, formats, timeZone, weekStartsOn, fromResetFcn)
  }

  @tailrec
  def parseTime(dateString: String, formats: Seq[String]): DateTime = {
    Try(DateTimeFormat.forPattern(formats.head).parseDateTime(dateString)) match {
      case Success(parsedTime) => parsedTime
      case _ if formats.tail.nonEmpty => parseTime(dateString, formats.tail)
      case _ => throw new IllegalArgumentException(s"Unable to parse $dateString with supported timezone formats")
    }
  }

  def parsePeriod(dateString: String, formats: Seq[String], dateTimeZone: DateTimeZone, weekStartsOn: Int,
                    intervalResetBoundaryFcn: Option[(Interval, DateTime) => DateTime] = None): DateTime = {
    val dateTime = dateString match {
      case "now" =>
        // Align to boundary for caching, since "now" is always changing
        val time = DateTime.now(dateTimeZone)
        intervalResetBoundaryFcn.map(_(Hourly, time)).getOrElse(time)
      case SecondsRegex(sign: String, value: String) =>
        val time = DateTime.now(dateTimeZone).plusSeconds((sign + value).toInt)
        intervalResetBoundaryFcn.map(_(Seconds, time)).getOrElse(time)
      case MinutesRegex(sign, value) =>
        val time = DateTime.now(dateTimeZone).plusMinutes((sign + value).toInt)
        intervalResetBoundaryFcn.map(_(Minutes, time)).getOrElse(time)
      case HoursRegex(sign, value) =>
        val time = DateTime.now(dateTimeZone).plusHours((sign + value).toInt)
        intervalResetBoundaryFcn.map(_(Hourly, time)).getOrElse(time)
      case DaysRegex(sign, value)  =>
        val time = DateTime.now(dateTimeZone).plusDays((sign + value).toInt)
        intervalResetBoundaryFcn.map(_(Daily, time)).getOrElse(time)
      case WeeksRegex(sign, value) =>
        val currentDayOfWeek = DateTime.now(dateTimeZone).dayOfWeek().get()
        val delta = if (weekStartsOn > currentDayOfWeek) (value.toInt + 1).toString else value
        val time = DateTime.now(dateTimeZone).plusWeeks((sign + delta).toInt)
        intervalResetBoundaryFcn.map(_(Weekly, time)).getOrElse(time)
      case MonthsRegex(sign, value) =>
        val time = DateTime.now(dateTimeZone).plusMonths((sign + value).toInt)
        intervalResetBoundaryFcn.map(_(Monthly, time)).getOrElse(time)
      case _ =>
        parseTime(dateString, formats)
    }
    // backend always process dateTime in UTC, convert to UTC time
    dateTime.toDateTime(DateTimeZone.UTC)
  }
}