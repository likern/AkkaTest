import java.nio.file.Path
import akka.actor.ActorSystem

/**
  * Created by victor on 11.09.16.
  */
object Main {
  def main(args: Array[String]): Unit = {
    if (args.size != 1) {
      val program = new Exception().getStackTrace.head.getFileName
      println(s"$program <dir>")
      println("Where dir - directory, containing mandatory")
      println("subdirectories [Image, Video,Page, Data]")
      sys.exit(1)
    }

    implicit val workDir = new RootDir(args(0))

    val ascendOrder = (p1: Path, p2: Path) => p1.getFileName.toString < p2.getFileName.toString
    val delNewline = (elem: String) => if (elem endsWith "\n") elem dropRight (1) else elem
    val withMeta = (dirType: DirName.DirName) => (elem: String) => (dirType, elem)

    val extractURI = (path: Path) => FSUtils extractURI path
    val extractFiles = (dirType: DirName.DirName) => (dirType, workDir.listFiles(dirType))

    val prepareLinks = (pair: (DirName.DirName, Vector[Path])) =>
      pair._2.sortWith(ascendOrder).map(extractURI).filter(_.isDefined)
        .map(_.get).map(delNewline).map(withMeta(pair._1))

    val images = prepareLinks(extractFiles(DirName.Image))
    val videos = prepareLinks(extractFiles(DirName.Video))
    val datas = prepareLinks(extractFiles(DirName.Data))
    val pages = prepareLinks(extractFiles(DirName.Page))

    val links = MergeVector(images, datas, videos, pages)

    val system = ActorSystem("system")
    implicit val replyWriter = system.actorOf(ResponseWriter.props, name = "writer")
    val manager = system.actorOf(DownloadManager.props(links), name = "manager")
    manager ! DownloadManager.Start
  }
}