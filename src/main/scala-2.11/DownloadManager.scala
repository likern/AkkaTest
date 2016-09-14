import akka.actor.{Actor, ActorRef, Props}
import akka.http.scaladsl.model.{HttpRequest, Uri}
import akka.event.Logging

class DownloadManager(vec: MergeVector[(DirName.DirName, String)])(writerActor: ActorRef) extends Actor {
  import akka.actor.OneForOneStrategy
  import akka.actor.SupervisorStrategy._
  import scala.concurrent.duration._

  val log = Logging(context.system, this)
  var count = vec.size

  def process(): Unit = {
    log.info("[DownloadManager] process")
    for {
      (dirType, addr) <- vec
      worker = context.actorOf(DataDownloader.props(writerActor))
    } worker ! PersistRequest(dirType, HttpRequest(uri=Uri(addr)))
  }

  // FIXME Write correct failure handling strategy for childs
  override val supervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1 minute) {
      case _: ArithmeticException      => Resume
      case _: NullPointerException     => Restart
      case _: IllegalArgumentException => Stop
      case _: Exception                => Escalate
    }

  def receive = {
    case DownloadManager.Start => {
      log.info("[DownloadManager] start")
      process()
    }
    case response: PersistResponse => {
      log.info(s"[DownloadManager] get response: ${response.response}")
      writerActor forward response
      count -= 1
      if (count == 0) {
        // all files were processed
        // can save leftovers http responses
        writerActor ! ResponseWriter.Flush
      }
    }
    case ResponseWriter.Finish => {
      // All job done, can terminate Actor System
      log.info("[DownloadManager] ALL JOB DONE")
      context.system.terminate()
    }
    case _ => log.info("[DownloadManager][FAILURE] Unexpected message received")
  }
}

object DownloadManager {
  case object Start
  def props(vec: MergeVector[(DirName.DirName, String)])(implicit writeActor: ActorRef): Props =
    Props(new DownloadManager(vec)(writeActor))
}