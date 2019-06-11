package io.github.nwtgck.gh_card

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer

object Main {
  def main(args: Array[String]): Unit = {
    implicit val system: ActorSystem = ActorSystem("trans-server")
    implicit val materializer: ActorMaterializer = ActorMaterializer()

    implicit val executionContext = system.dispatcher

    val route = {
      import akka.http.scaladsl.server.Directives._
      import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
      get {
        pathSingleSlash {
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<html><body>Hello world!</body></html>"))
        } ~
        path("ping") {
          complete("PONG!")
        } ~
        path("crash") {
          sys.error("BOOM!")
        }
      }
    }
    val host = "localhost"
    val port = 8080
    Http().bindAndHandle(route, host, port)
  }
}
