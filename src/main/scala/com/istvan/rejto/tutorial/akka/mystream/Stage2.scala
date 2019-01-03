package com.istvan.rejto.tutorial.akka.mystream

import akka.actor.{Actor, ActorLogging, Props}

object Stage2 {
  def props(): Props = {
    Props(new Stage2)
  }
}

class Stage2 extends Actor with ActorLogging {
  import StageCommunication._
  override def receive: Receive = {
    case HandleData(number, originalSender) =>
      log.info(number.toString)
      originalSender ! "Done"
  }
}