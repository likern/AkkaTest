import java.io.FileWriter
import java.nio.file.{FileAlreadyExistsException, Files, Path, Paths}

import ResponseWriter.Flush
import akka.actor.{Actor, ActorRef, Props}
import akka.actor.Actor.Receive
import akka.http.scaladsl.model.{HttpResponse, ResponseEntity}
import akka.event.Logging

import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.Map

/**
  * Created by victor on 13.09.16.
  */
class ResponseWriter(root: RootDir) extends Actor {
  val log = Logging(context.system, this)
  private val buffers: Map[DirName.DirName, ArrayBuffer[HttpResponse]] = Map()
  private val flushBorder = 100
  private var fileName = 0

  override def receive: Receive = {
    case data: PersistResponse => {
      log.info("Get PersistResponse in ResponseWriter")
      buffers.get(data.dirType) match {
        case Some(buffer) => {
          buffer += data.response
          if (buffer.length >= flushBorder) {
            flushToFile((data.dirType, buffer))
            buffer.clear()
          }
        }
        case None => buffers += (data.dirType -> ArrayBuffer(data.response))
      }
    }
    case Flush =>
      log.info("[ResponseWriter] get command [Flush]")
      for {
        pair <- buffers
      } flushToFile(pair)
    case _ => log.info("Get unexpected message in ResponseWriter")
  }

  private def flushToFile(pair: (DirName.DirName, ArrayBuffer[HttpResponse])) = {
    val path = Helpers.append(root.get(pair._1), Helpers.getPathFromDate())
    Files.notExists(path) match {
      case true => Files.createDirectories(path)
      case false => ; // do nothing
    }

    val newPath = Helpers.append(path, (fileName += 1) + ".data")
    Files.notExists(newPath) match {
      case true => Files.createFile(newPath)
      case false => throw new FileAlreadyExistsException(s"File: ${newPath.toString}")
    }

    val fileWriter = new FileWriter(newPath.toFile, true)
    for {
      elem <- pair._2
      content = pair._1.toString + elem.toString
    } {
      fileWriter.write(content)
      fileWriter.write('\n')
    }
    fileWriter.close()
  }
}

object ResponseWriter {
  // flush remaining existing HTTP responses
  case object Flush
  def props(implicit rootDir: RootDir): Props = Props(new ResponseWriter(rootDir))
}

