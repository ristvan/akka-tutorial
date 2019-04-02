package com.istvan.rejto.tutorial.akka.dataflow

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.istvan.rejto.tutorial.akka.dataflow.MessageHandover.Message

object MessageHandover {
    class Message() {}
    class SmartMessage() extends Message {}

    def props(messageDispatcher: ActorRef): Props = {
        Props(new MessageHandover(messageDispatcher))
    }
}

class MessageHandover(messageDispatcher: ActorRef) extends Actor with ActorLogging {

    override def receive: Receive = {
        case message: Message => {
            log.debug("Message Arrived")
            messageDispatcher ! message
        }
        case _ => {
            log.debug("Unknown Message")
        }
    }
}
