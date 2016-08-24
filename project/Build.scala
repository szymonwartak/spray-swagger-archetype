import sbt.Keys._
import sbt._
import spray.revolver.RevolverKeys

object Build extends Build with RevolverKeys {

  val akkaV = "2.3.3"
  val sprayV = "1.3.2"

  val coreSettings = Seq(
    scalaVersion := "2.11.8",
    javaOptions in reStart += "-Xmx2g",
    resolvers ++= Seq(
      Resolver.mavenLocal
    ),
    libraryDependencies ++= Seq(
      "org.json4s" %% "json4s-core" % "3.2.11",
      "org.json4s" %% "json4s-native" % "3.2.11",
      "org.json4s" %% "json4s-jackson" % "3.2.11",
      "com.github.swagger-spray" %% "swagger-spray" % "0.7.2",
      "org.webjars" % "swagger-ui" % "2.1.1",
      "com.typesafe.akka" %% "akka-actor"  % akkaV,
      "io.spray" %% "spray-can" % sprayV,
      "io.spray" %% "spray-json" % sprayV,
      "io.spray" %% "spray-routing" % sprayV,
      "org.slf4j" % "slf4j-simple" % "1.7.21"
    )
  )

  lazy val spray_swagger_archetype = Project("spray-swagger-archetype", file("."))
    .settings(coreSettings : _*)

}
