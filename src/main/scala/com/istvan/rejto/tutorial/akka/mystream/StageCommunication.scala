package com.istvan.rejto.tutorial.akka.mystream

import akka.actor.ActorRef

object StageCommunication {
  case class HandleData(number: Int, sender: ActorRef)
}
