package aia.ch04.s03

import java.io.File

/**
 * LogProcessingProtocol
 */
object LogProcessingProtocol {
  case class LogFile(file: File)

  case class Line(file: File, message: String)

  /** Returned by LogProcessor to signal the end of logging a file */
  case class FinishLogging(file: File)
}
