package com.example

import javax.ws.rs.Path

import akka.actor.ActorSystem
import akka.util.Timeout
import io.swagger.annotations._
import org.json4s.JsonAST.JObject
import org.json4s.jackson.JsonMethods._
import org.json4s.jackson.Serialization.{read, write}
import org.json4s.{DefaultFormats, Formats}
import spray.http.MediaTypes._
import spray.httpx.Json4sSupport
import spray.httpx.marshalling._
import spray.routing.{HttpService, SimpleRoutingApp}

import scala.concurrent.duration._


case class Complex(one: String, two: List[Int], three: Map[String, Int])

object RestApi extends SimpleRoutingApp with CheckExecutionService with App {
  implicit def json4sFormats: Formats = DefaultFormats

  startServer(interface = "0.0.0.0", port = 8080)(routes)
}


trait CheckExecutionService extends HttpService with SwaggerUiHttpService with PostRoute {
  implicit val system = ActorSystem("system")
  implicit val timeout = Timeout(5 seconds)

  lazy val routes = {
    postback ~
      swaggerUiRoutes ~ new SwaggerCheckExecutionService(system).routes
  }
}


@Api(value = "example", produces = "application/json")
@Path("/ze-route")
trait PostRoute extends HttpService with MetaToResponseMarshallers with Json4sSupport {
  @ApiOperation(value = "op_value", notes = "", nickname = "op nickname", httpMethod = "POST")
  @ApiImplicitParams(Array(new ApiImplicitParam(name = "param_name", value = "param value",
    required = true, dataType = "com.example.Complex", paramType = "body")))
  @ApiResponses(Array(new ApiResponse(code = 200, message = "api response message", response = classOf[Complex])))
  def postback = path("ze-route") {
    post {
      entity(as[JObject]) { jobj =>
        respondWithMediaType(`application/json`) {
          complete {
            val obj = compact(render(jobj))
            val fa = read[Complex](obj)
            parse(write(fa))
          }
        }
      }
    }
  }
}
