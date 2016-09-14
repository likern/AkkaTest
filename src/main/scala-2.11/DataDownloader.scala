import DataDownloader.Finish
import akka.actor.{Actor, ActorContext, ActorRef, Props}
import akka.actor.Actor.Receive
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, StatusCodes}
import akka.stream.ActorMaterializer
import akka.util.ByteString


/**
  * Created by victor on 11.09.16.
  */
class DataDownloader(actorRef: ActorRef) extends Actor {
  import akka.pattern.pipe
  import context.dispatcher

  val http = Http(context.system)
  final implicit val materializer = ActorMaterializer()

  override def receive: Receive = {
    case request: HttpRequest => http.singleRequest(request).pipeTo(self)
    case response: HttpResponse => {
      context.parent ! PersistResponse(DirName.Page, response)
      //context stop self
    }
//    case HttpResponse(StatusCodes.OK, headers, entity, _) => {
//      //log.info("Got response, body: " + entity.dataBytes.runFold(ByteString(""))(_ ++ _))
//      actorRef ! PersistResponse(DirName.Page, )
//    }
  }

}

object DataDownloader {
  case object Finish
  def props(actorRef: ActorRef): Props = Props(new DataDownloader(actorRef))
}
