package com.istvan.rejto.tutorial.akka.dataflow

import akka.actor.{Actor, ActorLogging, Props}
import com.istvan.rejto.tutorial.akka.dataflow.MessageHandover.SmartMessage

object MessageDispatcher {
    def props: Props = {
        Props(new MessageDispatcher)
    }
}

class MessageDispatcher extends Actor with ActorLogging {
    override def receive: Receive = {
        case msg: SmartMessage => {
            log.debug("SmartMessage arrived - MHO")
        }
    }
}
