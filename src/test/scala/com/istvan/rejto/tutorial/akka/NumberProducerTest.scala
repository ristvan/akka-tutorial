package com.istvan.rejto.tutorial.akka

import akka.actor.{Actor, ActorSystem}
import akka.testkit.TestKit
import com.typesafe.config.ConfigFactory
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, WordSpecLike}

import scala.concurrent.duration.FiniteDuration

object NumberProducerTest {
    val config = """
    akka {
      loglevel = "DEBUG"
    }
    """

    class DefaultConsumer extends Actor {
        override def receive: Receive = {
            case _ => None
        }
    }
}

class NumberProducerTest
    extends TestKit(
        ActorSystem(
            "MyTest",
            ConfigFactory.parseString(NumberProducerTest.config)
        )
    )
        with WordSpecLike
        with BeforeAndAfter
        with BeforeAndAfterAll {

    override def afterAll: Unit = {
        shutdown()
    }

    before {
        println("Start Test Case")
    }

    after {
        println("End Test Case")
    }

    "NumberProducer actor" should {
        val numberProducer = system.actorOf(NumberProducer.props(this.testActor))

        "produce number only between start and stop" in {
            numberProducer ! NumberProducer.StartProduction
            expectMsg(1)
            expectMsg(2)
            expectMsg(3)
            numberProducer ! NumberProducer.StopProduction
            expectNoMessage(FiniteDuration(1, "second"))
        }

        "during number production StartProduction will not do anything" in {
            numberProducer ! NumberProducer.StartProduction
            expectMsg(4)
            expectMsg(5)
            numberProducer ! NumberProducer.StartProduction
            expectMsg(6)
            expectMsg(7)
            numberProducer ! NumberProducer.StopProduction
            expectNoMessage(FiniteDuration(1, "second"))
        }
    }

    "The numbers during production" should {
        "be increasing by 1" in {
            val numberProducer = system.actorOf(NumberProducer.props(this.testActor))
            numberProducer ! NumberProducer.StartProduction
            expectMsg(1)
            expectMsg(2)
            expectMsg(3)
            expectMsg(4)
            expectMsg(5)
            expectMsg(6)
            expectMsg(7)
            numberProducer ! NumberProducer.StopProduction
            expectNoMessage(FiniteDuration(1, "second"))

        }
    }
}