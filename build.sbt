name := "gh-card"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.11.12"

val akkaHttpVersion = "10.0.15"

lazy val root = (project in file(".")).
  settings(
    libraryDependencies ++= Seq(
      // sbt from http://akka.io/docs/
      "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-xml" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % Test,

      // (from: https://github.com/scala/scala-module-dependency-sample)
      "org.scala-lang.modules" %% "scala-xml" % "1.0.6",

      // Slick
      "com.typesafe.slick" %% "slick" % "3.1.1",
      "org.slf4j" % "slf4j-nop" % "1.7.26",
      "com.h2database" % "h2" % "1.4.199",

      // scopt
      "com.github.scopt" %% "scopt" % "3.6.0",

      // ScalaTest
      "org.scalatest" %% "scalatest" % "3.0.7" % Test
    )
  )
