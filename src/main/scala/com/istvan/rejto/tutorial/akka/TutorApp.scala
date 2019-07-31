package com.istvan.rejto.tutorial.akka

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import akka.routing.{ScatterGatherFirstCompletedGroup, ScatterGatherFirstCompletedPool}
import com.istvan.rejto.tutorial.akka.mystream.{Stage1, Stage2}
import com.istvan.rejto.tutorial.akka.scatterandgather.{PoolWorker, ScatterGather, Worker, WorkerRegistry}

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

    def scatterAndGatherGroup(actorSystem: ActorSystem): Unit = {
        val workers = List(
            actorSystem.actorOf(Worker.props(4, 0), name = "worker1"),
            actorSystem.actorOf(Worker.props(4, 1), name = "worker2"),
            actorSystem.actorOf(Worker.props(4, 2), name = "worker3"),
            actorSystem.actorOf(Worker.props(4, 3), name = "worker4"),
        )
        val paths = workers.map(worker => worker.path.toString)
        val router = actorSystem.actorOf(ScatterGatherFirstCompletedGroup(paths, 4.seconds).props(), name = "GroupRouter")

        val scatterGatherGroup = actorSystem.actorOf(ScatterGather.props(router), "ScatterGatherGroup")
        scatterGatherGroup ! "5"
        scatterGatherGroup ! List(5, 6, 7, 8)
    }

    def scatterAndGatherPool(actorSystem: ActorSystem): Unit = {
        val registry = actorSystem.actorOf(Props(new WorkerRegistry), name = "WorkerRegistry")

        val pool = actorSystem.actorOf(
            new ScatterGatherFirstCompletedPool(5, 4.seconds).props(
                Props.create(classOf[PoolWorker], registry)
            )
        )

        val scatterGatherPooler = actorSystem actorOf(ScatterGather.props(pool), name = "ScatterGatherPool")

        scatterGatherPooler ! List(10, 11, 12, 13)
    }

    def main(args: Array[String]): Unit = {
        println("Start of My program")

        val actorSystem = ActorSystem.create("MyActorSystem")

//        scatterAndGatherGroup(actorSystem)
        scatterAndGatherPool(actorSystem)

        val reaper = actorSystem.actorOf(ActorReaper.props)

        val stage2 = actorSystem.actorOf(Stage2.props(), "stage2")
        val stage1 = actorSystem.actorOf(Stage1.props(stage2), "stage1")

        val act = actorSystem.actorOf(TutorApp.props(reaper, stage1), "tutor01")

        Thread.sleep(10000)
        act ! "Message"
    }
}