name := """play-slick-restful-api-seed"""

version := "1.1.0-beta"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.8"

scalacOptions ++= Seq("-feature", "-language:implicitConversions", "-language:postfixOps","-Xlog-implicits")

libraryDependencies ++= Seq(
  cache,
  filters,
  ws,
  specs2 % Test,
  "org.specs2" %% "specs2-matcher-extra" % "3.8",
  "mysql" % "mysql-connector-java" % "5.1.39",
  "com.typesafe.play" %% "play-slick" % "2.0.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "2.0.0",
  "com.typesafe.akka" %% "akka-actor" % "2.4.7",
  "ch.qos.logback" % "logback-classic" % "1.1.4"
)

libraryDependencies += evolutions

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
resolvers += "typesafe" at "http://repo.typesafe.com/typesafe/releases/"
resolvers += "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"


// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator

fork in run := true