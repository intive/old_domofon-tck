package domofon

import io.gatling.core.Predef._
import io.gatling.http.Predef._

trait Scenario {

  self: Feeders =>

  object Paths {
    val contacts = "/contacts"
    val contact: String => String = uuid => s"/contacts/$uuid"
    val deputy: String => String = uuid => s"/contacts/$uuid/deputy"
    val importance: String => String = uuid => s"/contacts/$uuid/important"
  }

  val headers = Map(
    "Content-Type" -> """application/json"""
  )

  val createContact = exec(
    http("create-contact")
      .post(Paths.contacts)
      .headers(headers + ("Accept" -> """application/json"""))
      .body(StringBody(i("@{contact}")))
      .check(status.is(200))
      .check(jsonPath("$.id").saveAs("response_id"))
  )

  val getContacts = exec(
    http("get-all-contacts")
      .get(Paths.contacts)
      .check(status.is(200))
  )

  val getContact = exec(
    http("get-contact")
      .get(Paths.contact(i("@{response_id}")))
      .headers(headers)
      .check(status.is(200))
  )

  val removeContact = exec(
    http("remove-contact")
      .delete(Paths.contact(i("@{response_id}")))
      .headers(headers)
      .check(status.is(200))
  )

  val getDeputy = exec(
    http("get-deputy")
      .get(Paths.deputy(i("@{response_id}")))
      .headers(headers)
      .check(status.is(200))
  )

  val addDeputy = exec(
    http("get-deputy")
      .put(Paths.deputy(i("@{response_id}")))
      .body(StringBody(i("@{deputy}")))
      .headers(headers)
      .check(status.is(200))
  )

  val noDeputy = exec(
    http("get-deputy")
      .get(Paths.deputy(i("@{response_id}")))
      .headers(headers)
      .check(status.is(404))
  )

  val removeDeputy = exec(
    http("remove-deputy")
      .delete(Paths.deputy(i("@{response_id}")))
      .headers(headers)
      .check(status.is(200))
  )

  val changeImportance = exec(
    http("make-important")
      .put(Paths.importance(i("@{response_id}")))
      .headers(headers)
      .body(StringBody(i("@{importance}")))
      .check(status.is(200))
  )

  val `create-contact-scenario` = scenario("Create Contact Scenario")
    .feed(contactFeeder)
    .exec(
      createContact,
      getContact,
      addDeputy,
      getDeputy,
      removeDeputy,
      noDeputy
    )

  val `update-contact-scenario` = scenario("Update Contact Scenario")
    .feed(contactFeeder)
    .exec(
      createContact,
      addDeputy,
      removeDeputy
    )
  private def i(v: => String): String = v.replaceAllLiterally("@", "$")
}
