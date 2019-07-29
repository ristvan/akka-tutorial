package com.istvan.rejto.tutorial.akka.scatterandgather

import akka.actor.{ActorLogging, ActorSystem}
import akka.routing.ScatterGatherFirstCompletedGroup
import akka.testkit.TestKit
import com.typesafe.config.ConfigFactory
import org.scalatest.WordSpecLike

import scala.concurrent.duration._

object TestScatterAndGather {
    val config = """
    akka {
      loglevel = "DEBUG"
    }
    """
}

class TestScatterAndGather extends TestKit(
    ActorSystem(
        "SaG_Test",
        ConfigFactory.parseString(TestScatterAndGather.config)
    )
) with WordSpecLike {
    "A" should {
        val w1 = system.actorOf(Worker.props(4, 0), name = "worker1")
        val w2 = system.actorOf(Worker.props(4, 1), name = "worker2")
        val w3 = system.actorOf(Worker.props(4, 2), name = "worker3")
        val w4 = system.actorOf(Worker.props(4, 3), name = "worker4")

        val paths = List(w1.path.toString, w2.path.toString, w3.path.toString, w4.path.toString)
        val scgRouter = system.actorOf(ScatterGatherFirstCompletedGroup(paths, within = 2.second).props(), "scgRouter")

        s"B0 ${w1.path.toString}" in {
            w1 ! SGReq(5)
            Thread.sleep(2000)
//            expectMsg(10.second, 1)
        }

        "B" in {
            scgRouter ! SGReq(5)
            expectMsg(1)
        }
    }

}
