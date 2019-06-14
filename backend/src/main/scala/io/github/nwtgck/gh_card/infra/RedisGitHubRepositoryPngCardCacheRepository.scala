package io.github.nwtgck.gh_card.infra

import akka.util.ByteString
import com.redis.RedisClientPool
import io.github.nwtgck.gh_card.domain.GitHubRepositoryPngCardCacheRepository

class RedisGitHubRepositoryPngCardCacheRepository(redisClientPool: RedisClientPool, ttl: Int) extends GitHubRepositoryPngCardCacheRepository{
  val keyPrefix: String = "repo-png-card"

  override def cache(repoName: String, useFullName: Boolean, width: Int, height: Int, png: ByteString): Unit = {
    val key = s"${keyPrefix}/${repoName}/fullname=${useFullName}/${width}x${height}"
    redisClientPool.withClient{client =>
      client.set(key, png.toArray)
      client.expire(key, ttl)
    }
  }

  override def get(repoName: String, useFullName: Boolean, width: Int, height: Int): Option[ByteString] = {
    val key = s"${keyPrefix}/${repoName}/fullname=${useFullName}/${width}x${height}"
    redisClientPool.withClient{client =>
      import com.redis.serialization._
      import Parse.Implicits._
      client.get[Array[Byte]](key).map(ByteString.fromArray)
    }
  }
}
