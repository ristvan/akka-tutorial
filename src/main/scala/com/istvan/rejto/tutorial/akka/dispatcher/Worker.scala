package com.istvan.rejto.tutorial.akka.dispatcher

import akka.actor.{Actor, ActorLogging}

case class Work(number: Int)

class Worker extends Actor with ActorLogging {
    override def receive: Receive = {
        case Work(number) =>
            log.info(number.toString + " is received." )
    }
}
