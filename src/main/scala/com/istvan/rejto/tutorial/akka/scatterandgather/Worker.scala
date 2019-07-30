package com.istvan.rejto.tutorial.akka.scatterandgather

import akka.actor.{Actor, ActorLogging, Props}

case class SGReq(number: Int)

object Worker {
    def props(divider: Int, modulo: Int): Props =
        Props(new Worker(divider, modulo))
}

class Worker(divider: Int, modulo: Int) extends Actor with ActorLogging {
    override def receive: Receive = {
        case SGReq(number) =>
            log.info(s"Message arrived: $number ($divider -> $modulo) from $sender()")
            if (number % divider != modulo)
                Thread.sleep(3000)
            log.info(s"send answer ($divider -> $modulo) to $sender()")
            sender ! modulo
    }
}
