package com.istvan.rejto.tutorial.akka

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import akka.routing.ScatterGatherFirstCompletedGroup
import com.istvan.rejto.tutorial.akka.mystream.{Stage1, Stage2}
import com.istvan.rejto.tutorial.akka.scatterandgather.{ScatterGather, Worker}

import scala.concurrent.duration._

class TutorApp(reaperActor: ActorRef, stage1: ActorRef) extends Actor with ActorLogging {
    override def receive: Receive = {
        case "Done" =>
            log.info("DONE")
            context.stop(self)
        case message: String =>
            log.info(message)
            stage1 ! Stage1.HandleData(6)
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

        val workers = List(
            as.actorOf(Worker.props(4, 0), name = "worker1"),
            as.actorOf(Worker.props(4, 1), name = "worker2"),
            as.actorOf(Worker.props(4, 2), name = "worker3"),
            as.actorOf(Worker.props(4, 3), name = "worker4"),
        )
        val paths = workers.map(worker => worker.path.toString)
        val router = as.actorOf(ScatterGatherFirstCompletedGroup(paths, 4.seconds).props(), name = "Router")

        val scatterGather = as.actorOf(ScatterGather.props(router), "ScatterGather")

        scatterGather ! "5"
        scatterGather ! List(5, 6, 7, 8)

        val reaper = as.actorOf(ActorReaper.props)

        val stage2 = as.actorOf(Stage2.props(), "stage2")
        val stage1 = as.actorOf(Stage1.props(stage2), "stage1")

        val act = as.actorOf(TutorApp.props(reaper, stage1), "tutor01")

        Thread.sleep(6000)
        act ! "Message"
    }
}