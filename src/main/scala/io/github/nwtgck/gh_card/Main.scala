package io.github.nwtgck.gh_card

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer

object Main {
  def main(args: Array[String]): Unit = {
    implicit val system: ActorSystem = ActorSystem("trans-server")
    implicit val materializer: ActorMaterializer = ActorMaterializer()

    val host = "localhost"
    val port = 8080
    Http().bindAndHandle(new Routing().route, host, port)
  }
}
