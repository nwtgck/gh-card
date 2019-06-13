package io.github.nwtgck.gh_card.infra

import io.github.nwtgck.gh_card.{GitHubApi, GitHubJson}
import io.github.nwtgck.gh_card.domain.{GitHubApiService, GitHubRepositoryJsonCacheRepository}

import scala.util.{Success, Try}

class DefaultGitHubApiService(gitHubRepositoryJsonCacheRepository: GitHubRepositoryJsonCacheRepository,
                              gitHubAuthOpt: Option[GitHubApi.GitHubAuth]) extends GitHubApiService {
  /**
    * Get a repository
    *
    * @param repoName
    * @return
    */
  override def getRepository(repoName: String): Try[GitHubJson.Repository] = {
    import spray.json._
    import GitHubJson.Protocol._

    // Get cached JSON
    val jsonOpt: Option[String] = gitHubRepositoryJsonCacheRepository.get(repoName)

    for {
      repoJson <- jsonOpt match {
        // If cache is available
        case Some(json) =>
          println(s"Cached used: ${repoName}")
          // Use cached JSON
          Success(json)
        case None       =>
          println(s"Not cache used: ${repoName}")
          // Get repository by call GitHub API
          GitHubApi.getRepository(repoName, gitHubAuthOpt)
      }
      // Cache JSON
      _    <- Success(gitHubRepositoryJsonCacheRepository.cache(repoName, repoJson))
      // Parse JSON to repo
      repo <- Try(repoJson.parseJson.convertTo[GitHubJson.Repository])
    } yield repo
  }
}
