package aia.ch04.s03

import java.io.File

import akka.actor._

/**
 * FileWatcher
 */
object FileFinder {
  def props(logProcSupervisor: ActorRef) =
    Props(new FileFinder(logProcSupervisor))
}

class FileFinder(logProcSupervisor: ActorRef) extends Actor with ActorLogging {
  import FileFinderProtocol._
  import LogProcessingProtocol._

  var sourceUriTarget = ""

  override def postStop() = {
    log.info("stop")
  }

  def receive = {
    case NewFile(sourceUri) => {
      val file = new File(sourceUri)
      sourceUriTarget = sourceUri
      logProcSupervisor ! LogFile(file)
    }

    case FinishLogging(file) => {
      log.info("receive finishing Logging")
      context.stop(self)
    }

  }
}
