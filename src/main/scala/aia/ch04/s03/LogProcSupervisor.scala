package aia.ch04.s03

import java.io.FileNotFoundException

import akka.actor.SupervisorStrategy.Resume
import akka.actor.{ActorLogging, OneForOneStrategy, Actor, Props}

/**
 * LogProcSupervisor
 */
object LogProcSupervisor {
  def props(writerSupervisorProps: Props) =
    Props(new LogProcSupervisor(writerSupervisorProps))
}

class LogProcSupervisor(writerSupervisorProps: Props)
  extends Actor with ActorLogging {
  override def supervisorStrategy = OneForOneStrategy() {
    case _: FileNotFoundException => Resume
  }

  val writerSupervisor = context.actorOf(writerSupervisorProps)
  val logProcessor = context.actorOf(LogProcessor.props(writerSupervisor))

  def receive = {
    case m =>
      logProcessor forward m
  }
}
