package aia.ch04.s02

import akka.actor._

/**
 * WatchDog
 */
object WatchDog {
  def props(master: ActorRef) = Props(new WatchDog(master))
}

class WatchDog(master: ActorRef) extends Actor with ActorLogging {
  context.watch(master)

  def receive = {
    case Terminated(actorRef) =>
      log.warning("Actor {} terminated", actorRef)
  }
}
