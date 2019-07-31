package com.istvan.rejto.tutorial.akka.scatterandgather

import akka.actor.{Actor, ActorLogging, ActorRef, Terminated}

case class RegisterWorker(worker: ActorRef)
case class WorkerModulo(modulo: Int)

class WorkerRegistry extends Actor with ActorLogging {
    var currentNumber = 0

    override def receive = {
        case RegisterWorker(worker) =>
            log.info(s"$worker has been registered.")
            context.watch(worker)
            worker ! WorkerModulo(currentNumber)
            currentNumber = currentNumber + 1
        case Terminated(worker) =>
            log.info(s"$worker is terminated")
    }
}
