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

class NumberProducer(consumer: ActorRef) extends Actor with ActorLogging with FSM[State, Cancellable] {
  import NumberProducer.StartProduction
  import NumberProducer.Produce
  import NumberProducer.StopProduction
  when(Inactive) {
    case Event(StartProduction, _) => {
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
      goto(Active).using(scheduler)
    }
  }
  when(Active) {
    case Event(Produce, timer) => {
      log.info("New number is produced")
      consumer ! 42
      stay().using(timer)
    }
    case Event(StopProduction, timer) => {
      timer.cancel()
      goto(Inactive).using(null)
    }
  }

  onTransition {
    case Inactive -> Active => {
      log.info("Production has been scheduled")
    }

    case Active -> Inactive => {
      log.info("Production schedule has been cancelled")
    }
  }

  startWith(Inactive, null)
}