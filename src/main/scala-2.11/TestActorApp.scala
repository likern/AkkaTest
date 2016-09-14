//import akka.actor._
//import akka.actor.SupervisorStrategy._
//import scala.concurrent.duration._
//import akka.util.Timeout
//import akka.event.LoggingReceive
//import akka.pattern.{ ask, pipe }
//import com.typesafe.config.ConfigFactory
//
//import akka.testkit.TestKit
//
///**
//  * Runs the sample
//  */
//object TestActorApp extends App {
//  import Worker._
//
//  val config = ConfigFactory.parseString("""
//    akka.loglevel = "DEBUG"
//    akka.actor.debug {
//      receive = on
//      lifecycle = on
//    }
//                                         """)
//
//  val system = ActorSystem("FaultToleranceSample", config)
//  val worker = system.actorOf(Props[Worker], name = "worker")
//  val listener = system.actorOf(Props[Listener], name = "listener")
//  // start the work and listen on progress
//  // note that the listener is used as sender of the tell,
//  // i.e. it will receive replies from the worker
//  worker.tell(Start, sender = listener)
//
//  //////////////////////////
//  val supervisor = system.actorOf(Props[Supervisor], "supervisor")
//
//  supervisor ! Props[Child]
//  val child = expectMsgType[ActorRef] // retrieve answer from TestKit’s testActor
//}
//
///**
//  * Listens on progress from the worker and shuts down the system when enough
//  * work has been done.
//  */
