package com.istvan.rejto.tutorial.akka

import java.util.concurrent.TimeUnit
import akka.actor.{Actor, ActorLogging, ActorRef, Cancellable, FSM, Props}
import scala.concurrent.duration.Duration

object NumberProducer {
    case class StartProduction()
    case class StopProduction()
    case class Produce()
    def props(consumer: ActorRef): Props = {
        Props(new NumberProducer(consumer))
    }
}

trait State
case object Inactive extends State
case object Active extends State

case class NumberProducerData(scheduler: Cancellable, number: Int)

class NumberProducer(consumer: ActorRef) extends Actor with ActorLogging with FSM[State, NumberProducerData] {
    import NumberProducer.StartProduction
    import NumberProducer.Produce
    import NumberProducer.StopProduction
    when(Inactive) {
        case Event(StartProduction, NumberProducerData(_, value)) =>
            log.info("Starting Production")
            val scheduler = context.system.scheduler.schedule(
                Duration.Zero,
                Duration.create(
                    50,
                    TimeUnit.MILLISECONDS
                ),
                self,
                Produce
            )(context.system.dispatcher)
            goto(Active).using(NumberProducerData(scheduler, value))
    }
    when(Active) {
        case Event(Produce, NumberProducerData(scheduler, value)) =>
            log.info("New number is produced: {}", value)
            consumer ! value
            stay().using(NumberProducerData(scheduler, value + 1))
        case Event(StopProduction, NumberProducerData(scheduler, value)) =>
            scheduler.cancel()
            goto(Inactive).using(NumberProducerData(null, value))
    }

    onTransition {
        case Inactive -> Active =>
            log.info("Production has been scheduled")

        case Active -> Inactive =>
            log.info("Production schedule has been cancelled")
    }

    startWith(Inactive, NumberProducerData(null, 1))
}