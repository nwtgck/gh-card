name := "gh-card"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.12.8"

val akkaHttpVersion = "10.1.8"

lazy val root = (project in file(".")).
  settings(
    // Skip test when sbt assembly
    // (from: http://www.shigemk2.com/entry/scala_aseembly_part2)
    test in assembly := {},
    // (from: https://qiita.com/ytanak/items/97ecc67786ed7c5557bb)
    assemblyMergeStrategy in assembly := {
      case PathList("javax", "servlet", xs @ _*)         => MergeStrategy.first
      case PathList(ps @ _*) if ps.last endsWith ".properties" => MergeStrategy.first
      case PathList(ps @ _*) if ps.last endsWith ".xml" => MergeStrategy.first
      case PathList(ps @ _*) if ps.last endsWith ".types" => MergeStrategy.first
      case PathList(ps @ _*) if ps.last endsWith ".class" => MergeStrategy.first
      case "application.conf"                            => MergeStrategy.concat
      case "unwanted.txt"                                => MergeStrategy.discard
      case x =>
        val oldStrategy = (assemblyMergeStrategy in assembly).value
        oldStrategy(x)
    },
    // (from: https://stackoverflow.com/a/39389112/2885946)
    assemblyOutputPath in assembly := file(s"target/jar/${name.value}.jar"),

    libraryDependencies ++= Seq(
      // sbt from http://akka.io/docs/
      "com.typesafe.akka" %% "akka-stream" % "2.5.23",
      "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-xml" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % Test,

      // Option parser
      "com.github.scopt" %% "scopt" % "3.7.1",
      // (from: https://github.com/scala/scala-module-dependency-sample)
      "org.scala-lang.modules" %% "scala-xml" % "1.1.1",
      // JSON parse
      "io.spray" %%  "spray-json" % "1.3.5",
      // Redis
      "redis.clients" % "jedis" % "3.0.1",

      // for SVG to PNG conversion
      // https://mvnrepository.com/artifact/org.apache.xmlgraphics/batik-all
      "org.apache.xmlgraphics" % "batik-all" % "1.9.1",

      // ScalaTest
      "org.scalatest" %% "scalatest" % "3.0.7" % Test
    )
  )
