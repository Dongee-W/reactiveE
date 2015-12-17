package aia.ch04.s02

import akka.actor.Actor

/**
 * LifeCycleHooks
 */
class LifeCycleHooks extends Actor {
  println("constructor")

  override def preStart() {println("preStart")}
  override def postStop() {println("postStop")}

  override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
    println("preRestart")
    super.preRestart(reason, message)
  }
  override def postRestart(reason: Throwable): Unit = {
    println("postRestart")
    super.postRestart(reason)
  }

  def receive = {
    case "restart" =>
      throw new IllegalStateException("force restart")
    case msg: AnyRef =>
      println("Working...")
      sender() ! msg
  }

}
