package io.github.nwtgck.gh_card.domain

import io.github.nwtgck.gh_card.GitHubJson

import scala.util.Try

trait GitHubApiService {
  /**
    * Get a repository
    * @param repoName
    * @return
    */
  def getRepository(repoName: String): Try[GitHubJson.Repository]
}
