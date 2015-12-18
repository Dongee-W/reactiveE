package aia.ch05.s04

import aia.ch05.s04.DataStructure._
import dispatch._

import scala.concurrent.ExecutionContext.Implicits.global

/**
 * WeatherService
 */
object IntelService {

  /** The classifiedIntel service needs to call out to many government
   *  services in parallel and use the quickest response.
   */
  def getGovernment(classifiedIntel: ClassifiedIntel): Future[ClassifiedIntel] = {
    val futureGovX: Future[Option[Government]] = {
      val govUrl = url("http://www.met.inf.cu/asp/genesis.asp?TB0=PLANTILLAS&TB1=INICIAL")
      val web = Http(govUrl OK as.String)

      web.map { response =>
        Some(Government(response.take(400)))
      }
    }

    val futureGovY: Future[Option[Government]] = {
      val govUrl = url("http://www.scala-lang.org/api/current/index.html#scala.util.Try")
      val web = Http(govUrl OK as.String)

      web.map { response =>
        Some(Government(response.take(400)))
      }
    }

    val futures: List[Future[Option[Government]]] =
      List(futureGovX, futureGovY)

    /** The Future.firstCompletedOf function creates a new Future out of the two
     * provided weather service future results. It’s important to note that
     * firstCompletedOf returns the first completed future. A future is
     * completed with a successful value or a failure.
     */
    val fastestResponse: Future[Option[Government]] =
      Future.firstCompletedOf(futures)

    /** Instead of firstCompletedOf we could use find. find takes some
     * futures and a predicate function to find a matching future and returns
     * a Future[Option[T]].
     */
    val fastestSuccessfulResponse: Future[Option[Government]] =
      Future.find(futures)(maybeGov => maybeGov.isDefined)
        .map(_.flatten)

    fastestSuccessfulResponse.map { govResponse =>
      classifiedIntel.copy(government = govResponse)
    }
  }

  /** The music and movie service need to be processed in parallel
   * and combined into a classifiedIntel when both results are available.
   */
  def getEntertainment(classifiedIntel: ClassifiedIntel): Future[ClassifiedIntel] = {
    val futureMu: Future[Option[Music]] = {
      val muUrl = url("http://www.mofa.gov.tw/en/News_Content.aspx?n=D63485FC2A6F4D3C&sms=CE9D6F5CD437EB7A&s=CDB41DE0841130D3")
      val web = Http(muUrl OK as.String)

      web.map { response =>
        Some(Music(response.take(400)))
      }
    }

    val futureMo: Future[Option[Movie]] = {
      val moUrl = url("http://www.hollywood.com/")
      val web = Http(moUrl OK as.String)

      web.map { response =>
        Some(Movie(response.take(400)))
      }
    }

    /** The following code first zips the future futureMu and futureMo
     *  together into a new that contains both results inside a tuple value.
     */
    futureMu.zip(futureMo).map{
      case(music, movie) =>
        val entert = Entertainment(music, movie)
        classifiedIntel.copy(entertainment = Some(entert))
    }
  }

  def getMilitary(classifiedIntel: ClassifiedIntel): Future[ClassifiedIntel] = {
    val list = List("http://www.ettoday.net/news/20151218/615348.htm",
                "http://www.cna.com.tw/news/afe/201512180262-1.aspx")

    val militaries = list.map { militaryUrl => {
      val muUrl = url(militaryUrl)
      Http(muUrl OK as.String)
    }}

    val future = Future.sequence(militaries)

    future.map{military =>
      val mili = Military(military.map(x => x.take(700)))
      classifiedIntel.copy(military = Some(mili))
    }
  }

  /** The method is a simpler version sequence of the traverse method.
   * The following example shows how getMilitary2 looks when we
    * use traverse instead.
   */
  def getMilitary2(classifiedIntel: ClassifiedIntel): Future[ClassifiedIntel] = {
    val list = List("http://www.ettoday.net/news/20151218/615348.htm",
      "http://www.cna.com.tw/news/afe/201512180262-1.aspx")


    val future = Future.traverse(list) { militaryUrl =>
      val muUrl = url(militaryUrl)
      Http(muUrl OK as.String)
    }

    future.map{military =>
      val mili = Military(military.map(x => x.take(700)))
      classifiedIntel.copy(military = Some(mili))
    }
  }

  def main(args: Array[String]): Unit = {


    val re = getGovernment(ClassifiedIntel("Little mouse", 3242))
    val re2 = re.flatMap(x => getEntertainment(x))
    val re3 = getMilitary2(ClassifiedIntel("Little mouse", 3242))
    re3.map(x => x.military.map(y => println(y.webs)))

  }
}
