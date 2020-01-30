package com.github.dgilling.datetimeroutes

/**
  * Created by derric on 5/12/16.
  */
trait CustomJodaRoutes extends JodaDateTimeRoutes
  with DefaultJodaFormats

object CustomJodaRoutes extends CustomJodaRoutes
