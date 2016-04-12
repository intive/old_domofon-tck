package domofon.mock.akka.server

import akka.actor.ActorSystem
import akka.http.scaladsl._
import akka.http.scaladsl.server._
import akka.stream._
import domofon.mock.akka.MockServer

import scala.util.{Success, Failure}

object Server extends App with MockServer {

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()

  import system.dispatcher

  val host = "127.0.0.1"
  val port = 8080

  Http().bindAndHandle(domofonRoute, host, port).onComplete {
    case Success(binding) =>
      println(s"Listening on http:/${binding.localAddress}")
    case Failure(e) =>
      println("Unable to bind, exiting...")
      System.exit(1)
  }

  println("Press ENTER to stop mock server")
  scala.io.StdIn.readLine()

  system.awaitTermination()

  System.exit(0)

}
