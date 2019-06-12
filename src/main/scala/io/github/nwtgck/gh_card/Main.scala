package io.github.nwtgck.gh_card

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import redis.clients.jedis.Jedis

object Main {
  def main(args: Array[String]): Unit = {
    implicit val system: ActorSystem = ActorSystem("trans-server")
    implicit val materializer: ActorMaterializer = ActorMaterializer()

    // Create Redis client
    val jedis: Jedis = new Jedis()


    // Create GitHub API Service
    val gitHubApiService: domain.GitHubApiService = new infra.DefaultGitHubApiService(
      gitHubRepositoryJsonCacheRepository = new infra.RedisGitHubRepositoryJsonCacheRepository(
        jedis,
        // TTL for repo JSON
        // 20 min
        // TODO: Hard code
        20 * 60
      )
    )

    // TODO: Hard code
    val host = "localhost"
    // TODO: Hard code
    val port = 8080
    Http().bindAndHandle(new Routing(gitHubApiService).route, host, port)
  }
}
