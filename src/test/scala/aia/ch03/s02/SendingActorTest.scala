package aia.ch03.s02

import aia.StopSystemAfterAll

import akka.actor.ActorSystem
import akka.testkit.TestKit
import org.scalatest.{MustMatchers, WordSpecLike}

/**
 * SendingActor01Test
 */
class SendingActorTest extends TestKit(ActorSystem("testsystem"))
  with WordSpecLike
  with MustMatchers
  with StopSystemAfterAll {

  "A Sending Actor" must {
    "send a message to an actor when it has finished" in {
      import aia.ch03.s02.SendingActor._
      val props = SendingActor.props(testActor)
      val sendingActor = system.actorOf(props, "SendingActor")

      val tickets = Vector(Ticket(1), Ticket(2), Ticket(3))
      val game = Game("Lakers vs Bulls", tickets)
      sendingActor ! game

      expectMsgPF() {
        case Game(_, ticketsE) =>
          ticketsE.size must be(game.tickets.size - 1)
      }
    }
  }
}
