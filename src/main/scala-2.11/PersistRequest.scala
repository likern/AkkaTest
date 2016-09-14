import akka.http.scaladsl.model.HttpRequest

/**
  * Created by victor on 13.09.16.
  */
case class PersistRequest(dirType: DirName.DirName, request: HttpRequest)
