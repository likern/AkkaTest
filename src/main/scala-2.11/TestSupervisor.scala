import java.nio.file.Path

import DownloadManager.Start
import akka.actor.{Actor, ActorRef, Props}
import akka.http.scaladsl.model.{HttpRequest, Uri}
import akka.event.Logging

class DownloadManager(vec: MergeVector[String])(writerActor: ActorRef) extends Actor {
  import akka.actor.OneForOneStrategy
  import akka.actor.SupervisorStrategy._
  import scala.concurrent.duration._

  val log = Logging(context.system, this)
  var count = vec.size

  def process(): Unit = {
    log.info("DownloadManager process")
    for {
      addr <- vec
      worker = context.actorOf(DataDownloader.props(writerActor))
    } worker ! PersistRequest(DirName.Page, HttpRequest(uri=Uri(addr)))
  }

  override val supervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1 minute) {
      case _: ArithmeticException      => Resume
      case _: NullPointerException     => Restart
      case _: IllegalArgumentException => Stop
      case _: Exception                => Escalate
    }

  def receive = {
    case Start => {
      log.info("DownloadManager start")
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
    case _ => log.info("Other message get")
  }
}

object DownloadManager {
  case object Start
  def props(vec: MergeVector[String])(implicit writeActor: ActorRef): Props =
    Props(new DownloadManager(vec)(writeActor))
}