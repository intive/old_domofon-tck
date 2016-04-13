package com.blstream
package domofon

trait Feeders {
  self: Generators =>

  def contactFeeder = Iterator.continually(Map("contact" -> generateContact.toString))
}
