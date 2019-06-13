package io.github.nwtgck.gh_card.domain

import akka.util.ByteString

trait GitHubRepositoryPngCardCacheRepository {
  def cache(repoName: String, useFullName: Boolean, width: Int, height: Int, png: ByteString): Unit
  def get(repoName: String, useFullName: Boolean, width: Int, height: Int): Option[ByteString]
}
