package io.github.nwtgck.gh_card

import scala.util.Try

object GitHubApi {
  case class GitHubAuth(gitHubClientId: String, gitHubClientSecret: String)

  /**
    * Get a repository
    * @param repoName
    * @param gitHubAuthOpt
    * @return
    */
  def getRepository(repoName: String, gitHubAuthOpt: Option[GitHubAuth]): Try[String] = {
    Try{
      val query: String = gitHubAuthOpt.map(a => s"?client_id=${a.gitHubClientId}&client_secret=${a.gitHubClientSecret}").getOrElse("")
      val source = scala.io.Source.fromURL(s"https://api.github.com/repos/${repoName}${query}")
      val json: String = source.mkString
      source.close()
      json
    }
  }
}
