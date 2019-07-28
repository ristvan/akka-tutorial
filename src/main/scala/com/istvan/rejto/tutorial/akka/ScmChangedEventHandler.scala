package com.istvan.rejto.tutorial.akka

import akka.actor.{Actor, ActorLogging, ActorRef, FSM, Props}
import com.istvan.rejto.tutorial.akka.ScmChangedEventHandler.{Done, Request1, Request2, Request3, Response1, Response2}

trait ScmState
object State1 extends ScmState
object State2 extends ScmState
object State3 extends ScmState
object State4 extends ScmState

case class Data(value: Int)

class ScmChangedEventHandler(communicator: ActorRef) extends Actor with ActorLogging with FSM[ScmState, Data]{
    def handleResponse1(code: Int, value: Int, state: ScmState) = {
        log.info(s"Response1 arrived: $code -> $value ($state)")
    }
    def handleResponse2(code: Int, value: Int, state: ScmState) = {
        log.info(s"Response2 arrived: $code -> $value ($state)")
        communicator ! Request3()
    }

    when(State1) {
        case Event(Response1(code), Data(value)) =>
            handleResponse1(code, value, stateName)
            goto(State2).using(Data(value + 1))
        case Event(Response2(code), Data(value)) =>
            handleResponse2(code, value, stateName)
            goto(State3).using(Data(value + 1))
    }
    when(State2) {
        case Event(Response2(code), Data(value)) =>
            handleResponse2(code, value, stateName)
            goto(State4).using(Data(value + 1))
    }
    when(State3) {
        case Event(Response1(code), Data(value)) =>
            handleResponse1(code, value, stateName)
            goto(State4).using(Data(value + 1))
    }
    when(State4) {
        case Event(Done, Data(value)) =>
            log.info("It should finish")
            stay()
    }
    startWith(State1, Data(1))
    communicator ! Request1()
    communicator ! Request2()
}

object ScmChangedEventHandler {
    case class Request1()
    case class Request2()
    case class Request3()
    case class Done()

    case class Response1(code: Int)
    case class Response2(code: Int)
    case class Response3(code: Int)

    def props(communicator: ActorRef): Props = {
        Props(new ScmChangedEventHandler(communicator))
    }
}