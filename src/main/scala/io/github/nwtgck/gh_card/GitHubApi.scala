package io.github.nwtgck.gh_card

import spray.json._

import scala.util.Try

object GitHubApi {
  case class Repository(description: String, language: String, stargazers_count: Int, forks_count: Int)

  // Implicit conversions
  object Protocol extends DefaultJsonProtocol {
    implicit val repositoryFormat = jsonFormat4(Repository)
  }

  /**
    * Get a repository
    * @param repoName
    * @return
    */
  def getRepository(repoName: String): Try[Repository] = {
    import Protocol._
    Try{
      val source = scala.io.Source.fromURL(s"https://api.github.com/repos/${repoName}")
      val json: String = source.mkString
      source.close()
      json.parseJson.convertTo[Repository]
    }
  }
}
