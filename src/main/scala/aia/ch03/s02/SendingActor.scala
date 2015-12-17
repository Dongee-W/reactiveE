package aia.ch03.s02

import akka.actor.{Props, Actor, ActorRef}

/**
 * SendingActor
 */
object SendingActor {
  def props(nextAgent: ActorRef) = Props(new SendingActor(nextAgent))
  case class Ticket(seat: Int)
  case class Game(name: String, tickets: Seq[Ticket])
}

class SendingActor(nextAgent: ActorRef) extends Actor {
  import SendingActor._
  def receive = {
    case game @ Game(_, tickets) =>
      nextAgent ! game.copy(tickets = tickets.tail)
  }
}
