import java.nio.file.Path

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.model.{HttpRequest, Uri}
import com.typesafe.config.ConfigFactory

/**
  * Created by victor on 11.09.16.
  */
object MainEntry {
  def main(args: Array[String]): Unit = {
    if (args.size != 2) {
      throw new IllegalArgumentException("Should provide two arguments")
    }

    implicit val workDir = new RootDir(args(0))
    val ascendOrder = (p1: Path, p2: Path) => p1.getFileName.toString < p2.getFileName.toString
    val extractURI = (path: Path) => FSUtils extractURI path

    val delNewline = (elem: String) => if (elem endsWith "\n") elem dropRight (1) else elem

    val images = workDir.listFiles(DirName.Image).sortWith(ascendOrder).map(extractURI).map(delNewline)
    val videos = workDir.listFiles(DirName.Video).sortWith(ascendOrder).map(extractURI).map(delNewline)
    val pages = workDir.listFiles(DirName.Page).sortWith(ascendOrder).map(extractURI).map(delNewline)
    val datas = workDir.listFiles(DirName.Data).sortWith(ascendOrder).map(extractURI).map(delNewline)

    val links = MergeVector(images, datas, videos, pages)

    val system = ActorSystem("system")
    implicit val replyWriter = system.actorOf(ResponseWriter.props, name = "writer")
    val manager = system.actorOf(DownloadManager.props(links), name = "manager")
    manager ! DownloadManager.Start
  }
//  val dr: RootDir = new RootDir("/")
//  for {
//    file <- dr.listFiles(DirName.Data)
//  } println("File: $file")
//
//
//  val config = ConfigFactory.parseString("""
//    akka.loglevel = "DEBUG"
//    akka.actor.debug {
//      receive = on
//      lifecycle = on
//    }
//                                         """)
//
//  val system = ActorSystem("DownloadSample", config)
//
//
//  val manager = system.actorOf(DownloadManager.props())
//  val fileWriter = system.actorOf(Props[DownloadManager], name = "writer")
//
//  val worker = system.actorOf(Props[Worker], name = "worker")
//  val listener = system.actorOf(Props[Listener], name = "listener")
//  // start the work and listen on progress
//  // note that the listener is used as sender of the tell,
//  // i.e. it will receive replies from the worker
//  worker.tell(Start, sender = listener)

}
