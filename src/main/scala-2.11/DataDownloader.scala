import akka.actor.{Actor, ActorRef, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpResponse
import akka.event.Logging
import akka.stream.ActorMaterializer

/**
  * Worker fow downloading data
  * Created by victor on 11.09.16.
  */
class DataDownloader(actorRef: ActorRef) extends Actor {
  import akka.pattern.pipe
  import context.dispatcher

  val log = Logging(context.system, this)
  final implicit val materializer = ActorMaterializer()
  val http = Http(context.system)
  var metadata = None: Option[DirName.DirName]

  override def receive: Receive = {
    case data: PersistRequest => {
      metadata = Some(data.dirType)
      http.singleRequest(data.request).pipeTo(self)
    }
    case response: HttpResponse => {
      context.parent ! PersistResponse(metadata.get, response)
      context stop self
    }
    case _ => {
      log.info(s"[DataDownloader][FAILURE] Received unexpected message to ${self.toString}. Stop actor")
      context stop self
    }
  }
}

object DataDownloader {
  def props(actorRef: ActorRef): Props = Props(new DataDownloader(actorRef))
}
