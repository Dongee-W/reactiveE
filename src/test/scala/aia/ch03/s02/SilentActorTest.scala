package aia.ch03.s02

import aia.StopSystemAfterAll
import akka.actor.{ActorSystem, Props}
import akka.testkit.{TestActorRef, TestKit}
import org.scalatest.{MustMatchers, WordSpecLike}

class SilentActorTest extends TestKit(ActorSystem("testsystem"))
  with WordSpecLike
  with MustMatchers
  with StopSystemAfterAll {


  "A Silent Actor" must {
    "change state when it receives a message, single threaded" in {
      import SilentActor.SilentMessage

      // Create a TestActorRef for single-threaded testing.
      val silentActor = TestActorRef[SilentActor]
      silentActor ! SilentMessage("whisper")

      // Get the underlying actor and assert the state.
      silentActor.underlyingActor.state must contain("whisper")
    }
    "change state when it receives a message, multi-threaded" in {
      import SilentActor._
      /**
       * The multi-threaded test uses the that ActorSystem is part of the
       * TestKit to create a SilentActor actor.
       *
       * An actor is always created from a Props object. The Props object
       * describes how the actor should be created. The simplest way to
       * create a Props is to create it with the actor type as its type
       * argument, in this case Props[SilentActor]. A Props created this
       * way will eventually create the actor using its default constructor.
       */
      val silentActor = system.actorOf(Props[SilentActor], "s3")

      /**
       * Since we now can’t just access the actor instance when using the
       * multi-threaded actor system, we’ll have to come up with another
       * way to see state change. For this a GetState message is added,
       * which takes an ActorRef. The TestKit has a testActor which
       * you can use to receive messages that you expect. The GetState
       * method we added is so we can have our SilentActor send its internal
       * state there. That way we can call the expectMsg method, which
       * expects one message to be sent to the testActor and asserts the
       * message; in this case it’s a Vector with all the data fields in it.
       */
      silentActor ! SilentMessage("whisper1")
      silentActor ! SilentMessage("whisper2")
      silentActor ! GetState(testActor)
      expectMsg(Vector("whisper1", "whisper2"))
    }
  }

}