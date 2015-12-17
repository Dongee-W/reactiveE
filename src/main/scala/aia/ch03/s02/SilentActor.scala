package aia.ch03.s02

import akka.actor.{Actor, ActorRef}

/**
 * SilentActor companion object which keeps related messages together
 */
object SilentActor {
  case class SilentMessage(data: String)
  case class GetState(receiver: ActorRef)
}

class SilentActor extends Actor {
  import SilentActor._

  /** In general it’s good practice to prefer vars in combination
    * with immutable data structures, instead of vals in combination
    * with mutable data structures.
    */
  var internalState = Vector[String]()

  def receive = {
    case SilentMessage(data) =>
      internalState = internalState :+ data

    /** The internal state is sent back to the in ActorRef the GetState
      * message, which in this case will be the testActor.
      */
    case GetState(receiver) => receiver ! internalState
  }

  def state = internalState
}
