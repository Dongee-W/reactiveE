package aia.ch03.s03

import aia.StopSystemAfterAll
import akka.actor.ActorSystem
import akka.testkit.{TestKit, ImplicitSender}
import org.scalatest.WordSpecLike

/**
 * EchoActorTest
 *
 * In this test we're going to use the ImplicitSender trait. This trait changes
 * the implicit sender in the test to the actor reference of the testkit.
 */
class EchoActorTest extends TestKit(ActorSystem("testsystem"))
  with WordSpecLike
  with ImplicitSender
  with StopSystemAfterAll {

  "Reply with the same message it receives without ask" in {
    val echo = system.actorOf(EchoActor.props, "echo")
    echo ! "some message"
    expectMsg("some message")
  }

}
