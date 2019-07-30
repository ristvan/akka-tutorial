package com.istvan.rejto.tutorial.akka.scatterandgather

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.pattern.pipe

import scala.concurrent.Future
//import akka.routing.ScatterGatherFirstCompletedGroup
//
//import scala.concurrent.duration._

class ScatterGather(router: ActorRef) extends Actor with ActorLogging {
    override def receive: Receive = {
        case numbers : List[Int] =>
            log.info(s"List Arrived : $numbers")
            numbers.foreach(number => self ! number.toString)

        case message : String =>
            log.info(s"String arrived : $message")
            router ! SGReq(message.toInt)
        case number : Int =>
            log.info(s"Number Arrived : $number (from: $sender())")
        case x =>
            log.info(s"Message Arrived : $x")
    }

    log.info("ScatterGather starts")

}

object ScatterGather {
    def props(router : ActorRef) : Props =
        Props(new ScatterGather(router))
}
