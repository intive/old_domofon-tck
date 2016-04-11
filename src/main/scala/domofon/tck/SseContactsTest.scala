package domofon.tck

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.scaladsl._
import akka.stream.testkit.TestSubscriber.OnNext
import akka.stream.testkit.scaladsl.TestSink
import de.heikoseeberger.akkasse.EventStreamUnmarshalling._
import de.heikoseeberger.akkasse.{EventStreamUnmarshalling, ServerSentEvent}
import domofon.tck.entities.SseUpdated
import org.scalatest.concurrent.ScalaFutures

import scala.concurrent._
import scala.concurrent.duration._
import spray.json._

trait SseContactsTest extends BaseTckTest with DomofonMarshalling with SprayJsonSupport with ScalaFutures {

  describe("GET /contacts/sse") {

    it("When contact is changed it notifies listeners") {
      val futureSse: Future[Source[ServerSentEvent, _]] = Source.single(Get("/contacts/sse"))
        .via(domofonRoute)
        .mapAsync(1)(Unmarshal(_).to[Source[ServerSentEvent, Any]])
        .runWith(Sink.head)

      implicit val patienceConfig = PatienceConfig(15.seconds)

      whenReady(futureSse) {
        sse =>
          val probe = sse.runWith(TestSink.probe)
          probe.ensureSubscription()
          val uuid = postContactRequest()
          val messages = probe.receiveWhile[ServerSentEvent](5.seconds) {
            case OnNext(r: ServerSentEvent) if r != ServerSentEvent.heartbeat => r
          }
          val update = probe.requestNext()
          probe.cancel()
          update.data.parseJson.convertTo[SseUpdated] shouldBe a[SseUpdated]
      }
    }

  }
}
