name := "gh-card"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.12.8"

val akkaHttpVersion = "10.1.8"

lazy val root = (project in file(".")).
  settings(
    libraryDependencies ++= Seq(
      // sbt from http://akka.io/docs/
      "com.typesafe.akka" %% "akka-stream" % "2.5.23",
      "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-xml" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % Test,

      // (from: https://github.com/scala/scala-module-dependency-sample)
      "org.scala-lang.modules" %% "scala-xml" % "1.1.1",

      "io.spray" %%  "spray-json" % "1.3.5",

      // for SVG to PNG conversion
      // https://mvnrepository.com/artifact/org.apache.xmlgraphics/batik-all
      "org.apache.xmlgraphics" % "batik-all" % "1.9.1",

      // ScalaTest
      "org.scalatest" %% "scalatest" % "3.0.7" % Test
    )
  )
