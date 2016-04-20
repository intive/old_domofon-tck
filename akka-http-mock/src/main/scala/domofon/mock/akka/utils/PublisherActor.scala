package domofon.mock.akka.utils

import akka.actor.{Actor, ActorLogging, ActorRef, Terminated}

import scala.collection.immutable

class PublisherActor extends Actor with ActorLogging {
  private[this] var subscribers = immutable.Set[ActorRef]()

  override def receive = {
    case x: PublisherActor.EventPublisherMessage =>
      x match {
        case PublisherActor.Updated =>
          broadcast()
        case PublisherActor.Subscribe =>
          val s = sender()
          subscribers += s
          context.watch(s)
        case PublisherActor.Unsubscribe =>
          unsubscribe(sender())
      }

    case Terminated(s) =>
      unsubscribe(s)
  }

  private[this] def broadcast(): Unit = {
    subscribers.foreach(_ ! PublisherActor.Updated)
  }

  private[this] def unsubscribe(s: ActorRef): Unit = {
    subscribers -= s
  }
}

case object PublisherActor {

  sealed trait EventPublisherMessage

  case object Updated extends EventPublisherMessage

  case object Subscribe extends EventPublisherMessage

  case object Unsubscribe extends EventPublisherMessage

}
