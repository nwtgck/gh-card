package io.github.nwtgck.gh_card.infra

import akka.util.ByteString
import io.github.nwtgck.gh_card.domain.GitHubRepositoryPngCardCacheRepository
import redis.clients.jedis.Jedis

class RedisGitHubRepositoryPngCardCacheRepository(jedis: Jedis, ttl: Int) extends GitHubRepositoryPngCardCacheRepository{
  val keyPrefix: String = "repo-png-card"

  override def cache(repoName: String, width: Int, height: Int, png: ByteString): Unit = {
    val key = s"${keyPrefix}/${repoName}/${width}x${height}"
    jedis.set(key.getBytes(), png.toArray)
    jedis.expire(key, ttl)
  }

  override def get(repoName: String, width: Int, height: Int): Option[ByteString] = {
    val key = s"${keyPrefix}/${repoName}/${width}x${height}"
    Option(jedis.get(key.getBytes())).map(ByteString.fromArray)
  }
}
