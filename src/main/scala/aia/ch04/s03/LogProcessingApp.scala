package aia.ch04.s03

import java.io.File

import akka.actor.{Terminated, Inbox, ActorSystem}

/**
 * LogProcessingApp
 */
object LogProcessingApp extends App {
  val source = Vector("src\\main\\resources\\tron.log", "src\\main\\resources\\3d.log")
  val target = new File("src\\main\\resources\\collection.log")
  val system = ActorSystem("logProcessing")

  val writerProps = Writer.props(target)
  val writerSuperProps = WriterSupervisor.props(writerProps)
  val logProcSuperProps = LogProcSupervisor.props(writerSuperProps)
  val topLevelProps = FileFinderSupervisor.props(source, logProcSuperProps)

  system.actorOf(topLevelProps)

}
