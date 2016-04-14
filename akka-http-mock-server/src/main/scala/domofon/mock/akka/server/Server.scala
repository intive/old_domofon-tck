package domofon.mock.akka.server

import akka.actor.ActorSystem
import akka.http.scaladsl._
import akka.http.scaladsl.model.Uri
import akka.http.scaladsl.server._
import akka.stream._
import domofon.mock.akka.MockServer

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.util.{Success, Failure}
import ch.megard.akka.http.cors.CorsDirectives._

object Server extends App {

  case class Config(listen: Uri = Uri("http://0.0.0.0:8080/"))

  implicit val uriReader = new scopt.Read[akka.http.scaladsl.model.Uri] {
    override def arity: Int = 1

    override def reads: (String) => Uri = Uri(_)
  }

  val parser = new scopt.OptionParser[Config]("domofon-akka-http-mock-server") {
    head("Domofon API Mock Server usign akka-http")

    opt[Uri]('l', "listen") action { (x, c) =>
      c.copy(listen = x)
    } text ("Address to listen in http://hostname:port/ format")

    help("help") text "Show usage help"

    override def showUsageOnError = true
  }

  parser.parse(args, Config()) match {
    case Some(conf) =>
      val host = conf.listen.authority.host.address()
      val port = conf.listen.authority.port

      println(s"Will try to bind on ${host}:${port}")

      implicit val system = ActorSystem()
      implicit val materializer = ActorMaterializer()

      import system.dispatcher

      val mockServer = MockServer(system, materializer)

      val routes: Route = cors() {
        mockServer.domofonRoute
      }

      Http().bindAndHandle(routes, host, port).onComplete {
        case Success(binding) =>
          println(s"Listening on ${binding.localAddress}")
          val hostname = if (binding.localAddress.getHostName.matches("^[0:\\.]*$")) {
            "localhost"
          } else {
            binding.localAddress.getHostName
          }
          val serverUrl = s"http://${hostname}:${binding.localAddress.getPort}"
          println()
          println(s"Open $serverUrl")
          println()
          println()
          println("To use Swagger Editor (preferred):")
          println(s"http://editor.swagger.io/#/?import=${serverUrl}/domofon.yaml")
          println()
          println("To use Swagger UI:")
          println(s"http://blstream.github.io/domofon-api/#swagger=${serverUrl}/domofon.yaml")
          println()
          println()

        case Failure(e) =>
          println("Unable to bind, exiting...")
          e.printStackTrace()
          System.exit(1)
      }

      println("Press ENTER to stop mock server")
      scala.io.StdIn.readLine()

      Await.result(system.terminate(), Duration.Inf)
      sys.exit(0)

    case None =>
      sys.exit(1)
  }

}
