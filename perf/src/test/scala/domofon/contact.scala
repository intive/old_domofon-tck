package domofon

import io.gatling.core.Predef._
import io.gatling.http.Predef._

trait Contacts {

  self: Feeders =>

  val path = "/contacts"

  val headers = Map(
    "Content-Type" -> """application/json""",
    "Accept" -> """application/json"""
  )

  val createContact = exec(
    http("create-contact")
      .post(path)
      .headers(headers)
      .body(StringBody(s"@{contact}".replaceAllLiterally("@", "$")))
      .check(status.is(200))
      .check(jsonPath("$.id").saveAs("response-id"))
  )

  val getContacts = exec(
    http("get-all-contacts")
      .get(path)
      .check(status.is(200))
  )

  val getContact = exec(
    http("get-contact")
      .get(s"$path/@{response-id}".replaceAllLiterally("@", "$"))
      .headers(headers)
      .check(status.is(200))
  )

  val removeContact = exec(
    http("remove-contact")
      .delete(s"$path/@{response-id}".replaceAllLiterally("@", "$"))
      .headers(headers)
      .check(status.is(200))
  )

  val scn = scenario("Create Contact")
    .feed(contactFeeder)
    .exec(
      createContact,
      getContact
    )
}
