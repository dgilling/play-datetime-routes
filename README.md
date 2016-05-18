# play-datetime-routes

[![Release](https://jitpack.io/v/dgilling/play-datetime-routes.svg)](https://jitpack.io/#dgilling/play-datetime-routes)

QueryString / Path Binder for Play 2.x

## Usage

Based originally on play-joda-routes-binder. Simple binder for using
Joda DateTime directly in the Play Routes File.

Supports DateTime, but also supports relative format includes seconds (s), minutes (m), hours (h), weeks (w) or “now” 
 
Examples:
query_param=-24h                    (last 24 hours from now)
query_param=now 	                (UTC Now)
query_param=2016-05-16T12:00.000    (Exact Date)

Add dependency and routesImport to your Build.scala

```scala
    resolvers += "jitpack" at "https://jitpack.io"

	libraryDependencies += "com.github.dgilling" % "play-datetime-routes" % "0.1.3"	
    
    routesImport += "com.github.dgilling.datetimeroutes.Routes._"
  )
```

Now you can bind query string and path parameters to DateTime.
```
GET     /list                 controllers.Application.index(date: org.joda.time.LocalDate)
GET     /entry/$date<[0-9]+>  controllers.Application.entry(date: org.joda.time.LocalDate)
```


```scala
package example

object MyRoutes {
  val myJodaRoutes = new com.github.dgilling.datetimeroutes.JodaRoutes {
    override val format: String = "yyyyMMdd'T'HH:mm:ss"
  }
}
```

```scala
routesImport += "example.MyRoutes.myJodaRoutes._"
```


## License
[Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0)
