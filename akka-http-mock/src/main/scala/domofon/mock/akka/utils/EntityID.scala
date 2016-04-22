package domofon.mock.akka.utils

import java.util.concurrent.atomic.AtomicInteger

import domofon.mock.akka.entities.EntityID

import scala.collection.concurrent.TrieMap
import scala.reflect.ClassTag

object EntityID {
  private[this] val msgId = new AtomicInteger(0)
  private[this] val catId = new AtomicInteger(0)
  private[this] val contId = new AtomicInteger(0)

  def forMessage: EntityID = msgId.incrementAndGet().toString
  def forCategory: EntityID = catId.incrementAndGet().toString
  def forContact: EntityID = contId.incrementAndGet().toString
}

