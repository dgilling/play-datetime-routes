package com.github.dgilling.datetimeroutes

/**
  * Created by derric on 5/16/16.
  */
trait JodaFormat {
  val format: String
}

trait DefaultJodaFormat extends JodaFormat {
  val format = "yyyyMMdd'T'HH:mm:ss.SSS"
}
