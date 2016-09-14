import java.nio.file.Path
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
  * Bunch of different helper functions
  * Created by victor on 13.09.16.
  */
object Helpers {
  def getPathFromDate(date: LocalDate = LocalDate.now()) = {
    date.format(DateTimeFormatter.ofPattern("uuuu/MM/dd"))
  }
}
