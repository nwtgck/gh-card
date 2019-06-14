package io.github.nwtgck.gh_card.infra

import com.redis.RedisClientPool
import io.github.nwtgck.gh_card.domain.GitHubRepositoryJsonCacheRepository

class RedisGitHubRepositoryJsonCacheRepository(redisClientPool: RedisClientPool, ttl: Int) extends GitHubRepositoryJsonCacheRepository {
  val keyPrefix = "repos-json"

  def cache(repoName: String, json: String): Unit = {
    val key = s"${keyPrefix}/${repoName}"
    redisClientPool.withClient{ client =>
      client.set(key, json)
      client.expire(key, ttl)
    }
  }

  def get(repoName: String): Option[String] = {
    val key = s"${keyPrefix}/${repoName}"
    redisClientPool.withClient{client =>
      client.get(key)
    }
  }
}
