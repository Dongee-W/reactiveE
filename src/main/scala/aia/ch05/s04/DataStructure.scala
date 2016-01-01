package aia.ch05.s04

/**
 * DataStructure
 */
object DataStructure {
  case class ClassifiedIntel(codeName: String,
                            target: Int,
                            government: Option[Government] = None,
                            military: Option[Military] = None,
                            entertainment: Option[Entertainment] = None)

  case class Government(web: String)

  case class Military(webs: List[String])

  case class Entertainment(music: Option[Music] = None, movie: Option[Movie] = None)

  case class Music(web: String)

  case class Movie(web: String)
}
