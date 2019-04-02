package com.istvan.rejto.tutorial.akka.dataflow

import akka.actor.{Actor, ActorSystem}
import akka.testkit.TestKit
import com.istvan.rejto.tutorial.akka.dataflow.MessageHandover.{SmartMessage}
import com.typesafe.config.ConfigFactory
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, WordSpecLike}

object DataFlowTest {
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

class DataFlowTest extends TestKit(
    ActorSystem(
        "DataFlowTest",
        ConfigFactory.parseString(DataFlowTest.config)
    )
)
    with WordSpecLike
    with BeforeAndAfter
    with BeforeAndAfterAll {

    override def afterAll: Unit = {
        shutdown()
    }

    before {
        println("Start DataFlow Test Case")
    }

    after {
        println("End DataFlow Test Case")
    }

    "DataFlow Test" should {
        "handle events correctly" in {
            val md = system.actorOf(MessageDispatcher.props)
            val mho = system.actorOf(MessageHandover.props(md))

            mho ! "Hello"
            mho ! new SmartMessage()
        }
    }
}
