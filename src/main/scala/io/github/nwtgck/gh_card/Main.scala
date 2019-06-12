package io.github.nwtgck.gh_card

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import redis.clients.jedis.Jedis
import scopt.OptionParser

object Main {
  case class CmdOption(redisHost: String)

  // Option parser
  val optParser: OptionParser[CmdOption] = new scopt.OptionParser[CmdOption]("") {
    opt[String]("redis-host") action { (v, option) =>
      option.copy(redisHost = v)
    } text "Redis host"
  }

  def main(args: Array[String]): Unit = {

    optParser.parse(args, CmdOption(
      redisHost = "localhost"
    )) match {
      case Some(option) =>
        implicit val system: ActorSystem = ActorSystem("gh-card")
        implicit val materializer: ActorMaterializer = ActorMaterializer()

        // Create Redis client
        val jedis: Jedis = new Jedis(option.redisHost)

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
        val host = "0.0.0.0"
        // TODO: Hard code
        val port = 8080
        Http().bindAndHandle(new Routing(gitHubApiService).route, host, port)
        println(s"Listening on ${port}...")
      case None =>
        // Command line parser error
        sys.exit(1)
    }
  }
}
