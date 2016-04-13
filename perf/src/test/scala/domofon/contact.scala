package com.blstream
package domofon

import io.gatling.core.Predef._
import io.gatling.http.Predef._

trait Contacts {

  self: Feeders =>

  val path = "/contacts"
  val uuid = "e0e0d673-0c38-4e55-9e1b-00df09c38ae2"

  val headers = Map("Content-Type" -> """application/json""")

  val createContact = exec(
    http("create-contact")
      .post(path)
      .headers(headers)
      .body(StringBody(s"@{contact}".replaceAllLiterally("@", "$")))
      .check(status.is(200))
  )

  val getContacts = exec(
    http("get-all-contacts")
      .get(path)
      .check(status.is(200))
  )

  val getContact = exec(
    http("get-contact")
      .get(s"$path/$uuid")
      .check(status.is(200))
  )

  val removeContact = exec(
    http("remove-contact")
      .delete(s"$path/$uuid")
      .check(status.is(200))
  )

  val scn = scenario("Create Contact")
    .feed(contactFeeder)
    .exec(
      createContact,
      getContact
    )
}
