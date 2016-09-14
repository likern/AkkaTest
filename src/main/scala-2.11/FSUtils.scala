import java.nio.file.{Files, Path}
import org.apache.commons.io.FileUtils

/**
  * Intended to be helper functions
  * specifically for I/O
  * Created by victor on 11.09.16.
  */
object FSUtils {
  def extractURI(path: Path): Option[String] = {
    Files.isRegularFile(path) && Files.isReadable(path) match {
      case true => Some(FileUtils.readFileToString(path.toFile, "UTF-8"))
      case false => None;
    }
  }

  def append(base: Path, path: String) = base.resolve(path)
}
