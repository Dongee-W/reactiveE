package aia.ch04.s03

import java.io.{FileWriter, File}

import aia.ch04.s03.LogProcessingProtocol.Line
import akka.actor.{ActorLogging, Props, Actor}

/**
 * Writer
 */
object Writer {
  def props(file: File): Props = Props(new Writer(file))

  case class WriteConfirmed(line: Line)
}

class Writer(file: File) extends Actor with ActorLogging {
  import LogProcessingProtocol._
  import Writer._

  def receive = {
    case line @ Line(time, message) => {
      val pw = new FileWriter(file, true)
      pw.write(line.toString + "\n")
      pw.close()
      sender() ! WriteConfirmed(line)
    }
  }
}
