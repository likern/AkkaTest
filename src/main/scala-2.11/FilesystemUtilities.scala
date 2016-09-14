import java.nio.file.{Files, LinkOption, Path}

import org.apache.commons.io.FileUtils



/**
  * Created by victor on 11.09.16.
  */
object FSUtils {
  def extractURI(path: Path): String = {
    require(Files.isRegularFile(path) && Files.isReadable(path))
    FileUtils.readFileToString(path.toFile, "UTF-8")
  }
}
