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
    "produce number only it is requested" in {
      val numberProducer = system.actorOf(NumberProducer.props(this.testActor))

      numberProducer ! NumberProducer.StartProduction
      expectMsg(1)
      expectMsg(2)
      expectMsg(3)
      numberProducer ! NumberProducer.StopProduction
      expectNoMessage(FiniteDuration(1, "second"))
    }
  }


}