package aia.ch03.s03

import akka.actor.{Props, Actor}

/**
 * EchoActor
 */
object EchoActor {
  def props = Props(new EchoActor)
}

class EchoActor extends Actor {
  def receive = {
    case msg =>
      sender() ! msg
  }
}
