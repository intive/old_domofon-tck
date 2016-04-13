package domofon

import io.gatling.core.Predef._
import io.gatling.http.Predef._

trait Scenario {

  self: Feeders =>

  val pathContacts = "/contacts"
  val pathContact: String => String = uuid => s"/contacts/$uuid"
  val pathDeputy: String => String = uuid => s"/contacts/$uuid/deputy"

  val headers = Map(
    "Content-Type" -> """application/json"""
  )

  val createContact = exec(
    http("create-contact")
      .post(pathContacts)
      .headers(headers + ("Accept" -> """application/json"""))
      .body(StringBody(i("@{contact}")))
      .check(status.is(200))
      .check(jsonPath("$.id").saveAs("response_id"))
  )

  val getContacts = exec(
    http("get-all-contacts")
      .get(pathContacts)
      .check(status.is(200))
  )

  val getContact = exec(
    http("get-contact")
      .get(pathContact(i("@{response_id}")))
      .headers(headers)
      .check(status.is(200))
  )

  val removeContact = exec(
    http("remove-contact")
      .delete(pathContact(i("@{response_id}")))
      .headers(headers)
      .check(status.is(200))
  )

  val getDeputy = exec(
    http("get-deputy")
      .get(pathDeputy(i("@{response_id}")))
      .headers(headers)
      .check(status.is(200))
  )

  val addDeputy = exec(
    http("get-deputy")
      .put(pathDeputy(i("@{response_id}")))
      .body(StringBody(i("@{deputy}")))
      .headers(headers)
      .check(status.is(200))
  )

  val scn = scenario("Create Contact")
    .feed(contactFeeder)
    .exec(
      createContact,
      getContact,
      addDeputy,
      getDeputy
    )

  private def i(v: => String): String = v.replaceAllLiterally("@", "$")
}
