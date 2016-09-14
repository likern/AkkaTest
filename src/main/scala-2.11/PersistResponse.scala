import akka.http.scaladsl.model.HttpResponse

/**
  * Created by victor on 13.09.16.
  */
case class PersistResponse(dirType: DirName.DirName, response: HttpResponse)
