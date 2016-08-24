package com.example


import akka.actor.ActorRefFactory
import com.github.swagger.spray.SwaggerHttpService
import com.github.swagger.spray.model.Info
import spray.http.StatusCodes
import spray.routing.{HttpService, Route}

import scala.reflect.runtime.{universe => ru}


/**
 * swaggerUiVersion Version of the swagger-ui bundle containing the resources, for example "2.1.1".
 * baseUrl Base URL where the application is hosted.
 * docsJson Endpoint for serving the api docs. Should NOT include the baseUrl.
 * uiPath Path to serve the swagger-ui. Should NOT include the baseUrl.
 */
trait SwaggerUiInfo {
  val swaggerUiVersion = "2.1.1"
  val uiPath = "swagger"
  val apiDocs = "api-docs"
  val baseUrl = "localhost:8080"
}


class SwaggerCheckExecutionService(context: ActorRefFactory) extends SwaggerHttpService with SwaggerUiInfo {
  implicit def actorRefFactory = context

  override val apiTypes = Seq(ru.typeOf[PostRoute])
  override val host = baseUrl
  override val apiDocsPath = apiDocs
  override val info = Info(
    description = "Examle spray swagger.",
    version = "0.1",
    title = "spray swagger")
}


trait SwaggerUiHttpService extends HttpService with SwaggerUiInfo {
  final def swaggerUiRoutes: Route =
    get {
      pathPrefix(uiPath.split("/").map(segmentStringToPathMatcher).reduceLeft(_ / _)) {
        // when the user hits the doc url, redirect to the index.html with api docs specified on the url
        pathEndOrSingleSlash { context =>
          context.redirect(s"$baseUrl/$uiPath/index.html?url=/$apiDocs/swagger.json", StatusCodes.TemporaryRedirect)
        } ~ getFromResourceDirectory(s"META-INF/resources/webjars/swagger-ui/$swaggerUiVersion")
      }
    }
}

