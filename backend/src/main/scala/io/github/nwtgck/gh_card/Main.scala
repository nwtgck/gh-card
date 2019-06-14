package io.github.nwtgck.gh_card

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.redis.RedisClientPool
import scopt.OptionParser

object Main {
  case class CmdOption(redisHost: String, gitHubClientId: Option[String], gitHubClientSecret: Option[String])

  // Option parser
  val optParser: OptionParser[CmdOption] = new scopt.OptionParser[CmdOption]("") {
    opt[String]("redis-host") action { (v, option) =>
      option.copy(redisHost = v)
    } text "Redis host"

    opt[String]("github-client-id") action { (v, option) =>
      option.copy(gitHubClientId = Some(v))
    } text "GitHub Client ID"

    opt[String]("github-client-secret") action { (v, option) =>
      option.copy(gitHubClientSecret = Some(v))
    } text "GitHub Client Secret"
  }

  def main(args: Array[String]): Unit = {

    optParser.parse(args, CmdOption(
      redisHost = "localhost",
      gitHubClientId = None,
      gitHubClientSecret = None
    )) match {
      case Some(option) =>
        implicit val system: ActorSystem = ActorSystem("gh-card")
        implicit val materializer: ActorMaterializer = ActorMaterializer()

        // Redis client pool
        // TODO: Hard code port
        val redisClientPool: RedisClientPool = new RedisClientPool(option.redisHost, 6379)

        // Pair of GitHub Client ID and Secret
        val gitHubAuthOpt: Option[GitHubApi.GitHubAuth] = for {
          id     <- option.gitHubClientId
          secret <- option.gitHubClientSecret
        } yield GitHubApi.GitHubAuth(id, secret)

        if (gitHubAuthOpt.isDefined) {
          println("GitHub Client ID and Secret was set")
        }

        // Create GitHub API Service
        val gitHubApiService: domain.GitHubApiService = new infra.DefaultGitHubApiService(
          gitHubRepositoryJsonCacheRepository = new infra.RedisGitHubRepositoryJsonCacheRepository(
            redisClientPool,
            // TTL for repo JSON
            // 20 min
            // TODO: Hard code
            20 * 60
          ),
          gitHubAuthOpt
        )

        // PNG cache
        val gitHubRepositoryPngCardCacheRepository = new infra.RedisGitHubRepositoryPngCardCacheRepository(
          redisClientPool,
          // TTL for repo JSON
          // 20 min
          // TODO: Hard code
          20 * 60
        )

        // Get route
        val route = new Routing(gitHubApiService, gitHubRepositoryPngCardCacheRepository).route

        // TODO: Hard code
        val host = "0.0.0.0"
        // TODO: Hard code
        val port = 8080
        Http().bindAndHandle(route, host, port)
        println(s"Listening on ${port}...")
      case None =>
        // Command line parser error
        sys.exit(1)
    }
  }
}
