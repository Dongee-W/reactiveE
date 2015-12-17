package aia.ch03.s02


import akka.actor.{Actor, ActorLogging, Props, ActorRef}

/**
 * Greeter02
 */
object Greeter02 {
  def props(listener: Option[ActorRef] = None) =
    Props(new Greeter02(listener))

  case class Greeting(string: String)
}

class Greeter02(listener: Option[ActorRef])
  extends Actor with ActorLogging {
  import aia.ch03.s02.Greeter02.Greeting

  def receive = {
    case Greeting(who) =>
      val message = "Hello " + who + "!"
      log.info(message)
      listener.foreach(_ ! message)
  }
}