package io.github.nwtgck.gh_card.infra

import io.github.nwtgck.gh_card.domain.GitHubRepositoryJsonCacheRepository
import redis.clients.jedis.Jedis

class RedisGitHubRepositoryJsonCacheRepository(jedis: Jedis, ttl: Int) extends GitHubRepositoryJsonCacheRepository {
  val keyPrefix = "repos-json"

  def cache(repoName: String, json: String): Unit = {
    val key = s"${keyPrefix}/${repoName}"
    jedis.set(key, json)
    jedis.expire(key, ttl)
  }

  def get(repoName: String): Option[String] = {
    val key = s"${keyPrefix}/${repoName}"
    Option(jedis.get(key))
  }
}
