package com.wangboo.play.mj

import scala.util.Random

/**
  * Created by wangboo on 2017/6/20.
  */
case class Mj(point: Int, color: Int, var roleId: Long = 0L) {
  val weight = color * 10 + point
}

object Mj {
  def create108(): List[Mj] = {
    var list = List[Mj]()
    for (p <- 1 to 9) {
      for (c <- 1 to 3) {
        list = Mj(p, c) :: Mj(p, c) :: Mj(p, c) :: Mj(p, c) :: list
      }
    }
    Random.shuffle(list)
  }
}

trait MjEvent {
  def rate = 1
}

case class MjEventPeng(evtMj: Mj, others: List[Mj]) extends MjEvent {
  override def rate: Int = 0
}

case class MjEventAnGang(others: List[Mj]) extends MjEvent

case class MjEventDianGang(evtMj: Mj, others: List[Mj]) extends MjEvent

case class MjEventBaGang(evtMj: Mj, peng: MjEventPeng) extends MjEvent
