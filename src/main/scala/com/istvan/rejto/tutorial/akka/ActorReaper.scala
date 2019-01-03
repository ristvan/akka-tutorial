package com.istvan.rejto.tutorial.akka

import akka.actor.{Actor, ActorLogging, ActorRef, Props, Terminated}

import scala.collection.mutable.ArrayBuffer

object ActorReaper {
  case class WatchMe()
  def props: Props =
    Props[ActorReaper]
}

class ActorReaper extends Actor with ActorLogging {
  import ActorReaper._

  val watched = ArrayBuffer.empty[ActorRef]

  def receive = {
    case WatchMe =>
      log.debug("New actor is registered: " + sender().toString())
      context.watch(sender())
      watched += sender()
    case Terminated(ref) =>
      watched -= ref
      log.debug("Actor is terminated: " + ref.toString())
      if (watched.isEmpty) {
        log.debug("No more Actors. The system will be terminated.")
        context.system.terminate()
      }
  }
}