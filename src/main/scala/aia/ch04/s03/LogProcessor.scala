package aia.ch04.s03

import java.io.File

import aia.ch04.s03.Writer.WriteConfirmed
import akka.actor.{ActorLogging, Props, Actor, ActorRef}

import scala.io.Source

/**
 * LogProcessor
 */
object LogProcessor {
  def props(writerSupervisorProps: ActorRef) = Props(new LogProcessor(writerSupervisorProps))
}

class LogProcessor(writerSupervisor: ActorRef) extends Actor with ActorLogging {
  import LogProcessingProtocol._
  var linesVec: Vector[Line] = Vector()
  var senders: List[(ActorRef, File)] = List()

  def receive = {
    case LogFile(file) => {
      val lines = Source.fromFile(file).getLines().toVector
      lines.foreach(line => {
        val transformed = Line(file, line.toUpperCase)
        linesVec = linesVec :+ transformed
        writerSupervisor ! transformed
      })
      senders = senders :+ (sender(), file)
    }

    case WriteConfirmed(line) => {
      linesVec = linesVec.filterNot(line == _)
      if(!linesVec.exists(_.file == line.file)) {
        val original = senders.find(x => x._2 == line.file)

        original match {
          case Some(x) =>
            log.info("Finish Logging file: " + line.file)
            x._1 ! FinishLogging(line.file)
          case None => log.error("Shit happened.")
        }

      }
    }
  }
}
