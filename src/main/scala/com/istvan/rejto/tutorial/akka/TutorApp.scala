package com.istvan.rejto.tutorial.akka

import akka.actor.{Actor, ActorLogging, ActorRef, Props, ActorSystem}
import com.istvan.rejto.tutorial.akka.mystream.{Stage1, Stage2}

class TutorApp(reaperActor: ActorRef, stage1: ActorRef) extends Actor with ActorLogging {
  override def receive: Receive = {
    case "Done" => {
      log.info("DONE")
      context.stop(self)
    }
    case message: String => {
      log.info(message)
      stage1 ! Stage1.HandleData(6)
    }
  }

  reaperActor ! ActorReaper.WatchMe
}

object TutorApp {
  def props(reaper: ActorRef, stage1: ActorRef): Props = {
    Props(new TutorApp(reaper, stage1))
  }

  def main(args: Array[String]): Unit = {
    println("Start of My program")
    val as = ActorSystem.create("MyActorSystem")
    val reaper = as.actorOf(ActorReaper.props)

    val stage2 = as.actorOf(Stage2.props(), "stage2")
    val stage1 = as.actorOf(Stage1.props(stage2), "stage1")

    val act = as.actorOf(TutorApp.props(reaper, stage1), "tutor01")


    act ! "Message"
  }
}