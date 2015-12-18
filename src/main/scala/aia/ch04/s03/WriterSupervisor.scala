package aia.ch04.s03

import java.io.IOException

import akka.actor.SupervisorStrategy.Stop
import akka.actor.{ActorLogging, OneForOneStrategy, Actor, Props}

/**
 * WriterSupervisor
 */
object WriterSupervisor {
  def props(writerProps: Props) = Props(new WriterSupervisor(writerProps))
}
class WriterSupervisor(writerProps: Props) extends Actor with ActorLogging {
  override def supervisorStrategy = OneForOneStrategy() {
    case _:IOException => Stop
  }

  val writer = context.actorOf(writerProps)

  def receive = {
    case m => {
      writer forward m
    }

  }
}
