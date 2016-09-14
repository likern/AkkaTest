import java.nio.file._
import scala.collection.JavaConverters._
import DirName.DirName

/**
  * Created by victor on 11.09.16.
  */
class RootDir(baseDir: String) {
  private val dirs = init(baseDir)

  def init(dirPath: String) = {
    val path = Paths.get(dirPath)
    if (!Files.isDirectory(path)) {
      throw new Exception("Not a directory")
    }

    try {
      val stream: DirectoryStream[Path] = Files.newDirectoryStream(path)
      val paths =
        (for {
          entry <- stream.asScala.toVector
          if Files.isDirectory(entry)
          filename = entry.getFileName.toString
          if DirName.values exists (_.toString == filename)
        } yield DirName.withName(filename) -> entry).toMap

      if (paths.size != DirName.values.size) {
        throw new Exception("Not found some paths")
      }
      paths
    } catch {
      case ex: DirectoryIteratorException => throw ex.getCause
    }
  }

  def listFiles(dir: DirName): Vector[Path] = {
    try {
      val stream: DirectoryStream[Path] = Files.newDirectoryStream(dirs(dir))
      for {
        entry <- stream.asScala.toVector
      } yield entry
    } catch {
      case ex: DirectoryIteratorException => throw ex.getCause
    }
  }

  def get(dirType: DirName.DirName) = {
    dirs(dirType)
  }
}

object DirName extends Enumeration {
  type DirName = Value
  val Page = Value("Page")
  val Video = Value("Video")
  val Data = Value("Data")
  val Image = Value("Image")
}
