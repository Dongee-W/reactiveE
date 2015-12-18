package aia.ch04.s02

import aia.StopSystemAfterAll
import akka.actor.{PoisonPill, Props, ActorSystem}
import akka.testkit.TestKit
import org.scalatest.WordSpecLike

/**
 * WatchDogTest
 */
class WatchDogTest extends TestKit(ActorSystem("testsystem"))
  with WordSpecLike
  with StopSystemAfterAll {

  "A WatchDog" must {
    "response to the death of its Master" in {

      val dogsMaster = system.actorOf(Props[Master], "master")
      val watchDog = system.actorOf(WatchDog.props(dogsMaster), "wd")

      dogsMaster ! PoisonPill
    }
  }

}
