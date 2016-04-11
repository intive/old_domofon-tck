package domofon.tck

import akka.http.scaladsl.model.{MediaTypes, StatusCodes}
import akka.http.scaladsl.server._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.{FunSpec, Matchers}
import spray.json.JsObject

trait BaseTckTest extends FunSpec with Matchers with ScalatestRouteTest {
  def domofonRoute: Route
}
