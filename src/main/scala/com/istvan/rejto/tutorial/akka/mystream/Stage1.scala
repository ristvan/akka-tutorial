package com.istvan.rejto.tutorial.akka.mystream

import akka.actor.{Actor, ActorLogging, ActorRef, Props}

object Stage1 {
  case class HandleData(number: Int)

  def props(nextStage: ActorRef): Props = {
    Props(new Stage1(nextStage))
  }
}

class Stage1(nextStage: ActorRef) extends Actor with ActorLogging {
  override def receive: Receive = {
    case Stage1.HandleData(number) =>
      log.info(number.toString)
      nextStage ! StageCommunication.HandleData(2 * number, sender())
  }
}