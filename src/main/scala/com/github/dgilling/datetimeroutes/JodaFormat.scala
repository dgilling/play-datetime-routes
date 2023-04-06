package com.github.dgilling.datetimeroutes

/**
  * Created by derric on 5/16/16.
  */
trait JodaFormats {
  val formats: Seq[String]
}

trait DefaultJodaFormats extends JodaFormats {
  val formats = Seq("yyyy-MM-dd'T'HH:mm:ss.SSS", "yyyy-MM-dd'T'HH:mm:ss.SSSZ",
    "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd'T'HH:mm:ssZ",
    "yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS","yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSSZ"
  )
}
