package com.istvan.rejto.tutorial.akka.scatterandgather

import akka.actor.{Actor, ActorLogging, ActorRef, Props}

object PoolWorker {
    def props(workerRegistry: ActorRef) : Props =
        Props(new PoolWorker(workerRegistry))
}

class PoolWorker(workerRegistry: ActorRef) extends Actor with ActorLogging {
    val divider = 4
    var modulo = 0
    override def receive: Receive = {
        case x: String =>
            log.info("message")
        case SGReq(number) =>
            log.info(s"SGReq arrived with $number")
            if (number % divider != modulo)
                Thread.sleep(1000)
            log.info(s"send answer ($divider -> $modulo) to $sender()")
            sender ! modulo
        case WorkerModulo(modulo) =>
            log.info(s"modulo received: $modulo")
            this.modulo = modulo

    }

    log.info("Register into WorkerRegistry")
    workerRegistry ! RegisterWorker(self)
}


