package io.github.nwtgck.gh_card.domain

trait GitHubRepositoryJsonCacheRepository {
  def cache(repoName: String, json: String): Unit
  def get(repoName: String): Option[String]
}
