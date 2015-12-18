package aia.ch04.s03

import aia.ch04.s03.FileFinderProtocol.NewFile
import akka.actor._

/**
 * FileWatchingSupervisor
 */
object FileFinderSupervisor {
  def props(sources: Vector[String], logProcSuperProps: Props) =
    Props(new FileFinderSupervisor(sources, logProcSuperProps))
}

class FileFinderSupervisor(sources: Vector[String],
                              logProcSuperProps: Props)
  extends Actor with ActorLogging {

  var fileFinders: Vector[ActorRef] = sources.map { source =>
    val logProcSupervisor = context.actorOf(logProcSuperProps)
    val fileFinder = context.actorOf(FileFinder.props(logProcSupervisor))

    fileFinder ! NewFile(source) // For test purpose

    context.watch(fileFinder)
    fileFinder
  }

  def receive = {
    case Terminated(fileFinder) =>
      log.info("fileWatcher Terminated..")
      fileFinders = fileFinders.filterNot(w => w == fileFinder)
      if (fileFinders.isEmpty) context.system.terminate()
  }
}
