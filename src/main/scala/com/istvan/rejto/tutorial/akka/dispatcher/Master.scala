package com.istvan.rejto.tutorial.akka.dispatcher

import akka.actor.{Actor, Props, Terminated}
import akka.routing.{ActorRefRoutee, RoundRobinRoutingLogic, Router}

class Master extends Actor {
    var router : Router = {
        val routees = Vector.fill(5) {
            val r = context.actorOf(Props[Worker])
            context watch r
            ActorRefRoutee(r)
        }
        Router(RoundRobinRoutingLogic(), routees)
    }

    def receive : PartialFunction[Any, Unit] = {
        case w: Work ⇒
            router.route(w, sender())
        case Terminated(a) ⇒
            router = router.removeRoutee(a)
            val r = context.actorOf(Props[Worker])
            context.watch(r)
            router = router.addRoutee(r)
    }
}