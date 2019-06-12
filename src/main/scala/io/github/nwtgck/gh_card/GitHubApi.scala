package io.github.nwtgck.gh_card

import scala.util.Try

object GitHubApi {
  /**
    * Get a repository
    * @param repoName
    * @return
    */
  def getRepository(repoName: String): Try[String] = {
    Try{
      val source = scala.io.Source.fromURL(s"https://api.github.com/repos/${repoName}")
      val json: String = source.mkString
      source.close()
      json
    }
  }
}
