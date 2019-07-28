package com.istvan.rejto.tutorial.akka

import akka.actor.ActorSystem
import akka.testkit.TestKit
import com.istvan.rejto.tutorial.akka.ScmChangedEventHandler.{Request1, Request2, Request3, Response1, Response2}
import com.typesafe.config.ConfigFactory
import org.scalatest.WordSpecLike

object ScmChangedEventHandlerTest {
    val config = """
    akka {
      loglevel = "DEBUG"
    }
    """
}

class ScmChangedEventHandlerTest extends TestKit(
    ActorSystem(
        "MyTest",
        ConfigFactory.parseString(NumberProducerTest.config)
    )
) with WordSpecLike {
    "ScmChangedEvent Handler actor" should {
        val scmChangedEventHandler = system.actorOf(ScmChangedEventHandler.props(this.testActor), name = "test1")

        "handle when answer to the first request arrives sooner" in {
            expectMsg(Request1())
            expectMsg(Request2())

            scmChangedEventHandler ! Response1(6)
            scmChangedEventHandler ! Response2(28)
            expectMsg(Request3())

            expectNoMessage()
        }
    }

//    "Other ScmChangedEvent Handler actor" should {
//        val scmChangedEventHandler = system.actorOf(ScmChangedEventHandler.props(this.testActor), name = "test2")
//
//        "handle when answer to the second request arrives sooner" in {
//            expectMsg(Request1())
//            expectMsg(Request2())
//
//            scmChangedEventHandler ! Response2(83)
//            expectMsg(Request3())
//
//            scmChangedEventHandler ! Response1(28)
//
//            expectNoMessage()
//        }
//    }
}
