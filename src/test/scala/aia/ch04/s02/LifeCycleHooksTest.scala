package aia.ch04.s02

import aia.StopSystemAfterAll
import akka.actor.{Props, ActorSystem}
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.WordSpecLike

/**
 * LifeCycleHooksTest
 */
class LifeCycleHooksTest extends TestKit(ActorSystem("testsystem"))
  with WordSpecLike
  with ImplicitSender
  with StopSystemAfterAll {

  "A complete actor life cycle" in {
    val lifeCycleActor = system.actorOf(
      Props[LifeCycleHooks], "LifeCycleHooks")
    lifeCycleActor ! "restart"
    lifeCycleActor ! "msg"
    expectMsg("msg")
    system.stop(lifeCycleActor)

    /* The sleep just before the stop makes sure
     *  that we can see the postStop happening.
     */
    Thread.sleep(1000)
  }
}
