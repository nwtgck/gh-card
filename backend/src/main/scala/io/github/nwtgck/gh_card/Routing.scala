package io.github.nwtgck.gh_card

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.model.{ContentType, HttpEntity, HttpResponse, MediaTypes, StatusCodes}
import akka.util.ByteString

import scala.util.Success
import scala.xml.Elem

class Routing(gitHubApiService: domain.GitHubApiService,
              gitHubRepositoryPngCardCacheRepository: domain.GitHubRepositoryPngCardCacheRepository) {

  def convertSvgToPng(svg: Elem, width: Int, height: Int): ByteString = {
    import org.apache.batik.transcoder.{SVGAbstractTranscoder, TranscoderInput, TranscoderOutput}
    import org.apache.batik.transcoder.image.PNGTranscoder

    val istream = new ByteArrayInputStream(svg.toString.getBytes("utf-8"))
    val byteOut = new ByteArrayOutputStream()
    val ostream = byteOut

    val input = new TranscoderInput(istream)
    val output = new TranscoderOutput(ostream)
    val pngTranscoder = new PNGTranscoder()
    pngTranscoder.addTranscodingHint(SVGAbstractTranscoder.KEY_WIDTH, width.toFloat)
    pngTranscoder.addTranscodingHint(SVGAbstractTranscoder.KEY_HEIGHT, height.toFloat)

    // Convert
    pngTranscoder.transcode(input, output)

    // Get .png byte string
    ByteString.fromArray(byteOut.toByteArray)
  }

  val route: Route =
    get {
      pathSingleSlash {
        complete("e.g. /repos/rust-lang/rust.svg")
      } ~
      path("repos" / Remaining) { repoNameWithExt =>
        parameter("fullname".?) { fullname: Option[String] =>
          // Whether include owner or not
          val useFullName: Boolean = fullname.isDefined

          println(s"repoNameWithExt: ${repoNameWithExt}, useFullName: ${useFullName}")
          val reg = """(.+)/(.+)\.(svg|png)""".r
          repoNameWithExt match {
            case reg(ownerName, shortRepoName, extension) =>
              println(s"ownerName: ${ownerName}, repoName: ${shortRepoName}")
              gitHubApiService.getRepository(s"${ownerName}/${shortRepoName}") match {
                case Success(repo) =>
                  // Repo name in an card
                  val repoNameInImage: String = if (useFullName) s"${ownerName}/${shortRepoName}" else shortRepoName

                  // Generate SVG image
                  val svg = GitHubRepositorySvgGenerator.generateSvg(
                    repoNameInImage,
                    repo.language,
                    repo.description.getOrElse(""),
                    repo.stargazers_count,
                    repo.forks_count
                  )

                  extension match {
                    case "svg" =>
                      complete(HttpResponse(
                        StatusCodes.OK,
                        List.empty,
                        HttpEntity(
                          ContentType(MediaTypes.`image/svg+xml`),
                          ByteString.fromString(svg.toString)
                        )
                      ))
                    case "png" =>
                      // TODO: Hard code width and height
                      val width  = 450 * 3
                      val height = 160 * 3
                      // Usage cache or
                      val pngByteString: ByteString = gitHubRepositoryPngCardCacheRepository
                        .get(s"${ownerName}/${shortRepoName}", useFullName, width, height)
                        .getOrElse({
                          // Convert SVG to png
                          val png = convertSvgToPng(svg, width, height)
                          // Cache PNG
                          gitHubRepositoryPngCardCacheRepository.cache(s"${ownerName}/${shortRepoName}", useFullName, width, height, png)
                          png
                        })

                      complete(HttpResponse(
                        StatusCodes.OK,
                        List.empty,
                        HttpEntity(
                          ContentType(MediaTypes.`image/png`),
                          pngByteString
                        )
                      ))
                  }
                case _ =>
                  // TODO: Fail response
                  complete("Internal error in the request")
              }
            case _ =>
              // TODO: Fail response
              complete("Invalid request")
          }
        }
      }
    }
}
