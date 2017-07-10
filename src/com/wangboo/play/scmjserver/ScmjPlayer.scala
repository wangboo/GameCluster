package com.wangboo.play.scmjserver

import com.wangboo.play.mj._

/**
  * Created by wangboo on 2017/6/20.
  */
class ScmjPlayer(roleId:Long, gate:String) {

  var isReady = false
  var pais = List[Mj]()
  var events = List[MjEvent]()

  def addMj(mj:Mj):Unit = {
    pais = mj :: pais
    pais = pais.sortBy(_.weight)
  }

  def pengs:List[MjEventPeng] = events.filter(_.isInstanceOf[MjEventPeng]).map(_.asInstanceOf[MjEventPeng])

  def getEvents[E >: MjEvent]: List[E] = {
    // 是否有巴杠
    var list:List[E] = pengs.foldLeft(List[MjEventBaGang]()) { (acc, evt) =>
      pais.find(_.weight == evt.evtMj.weight) match {
        case Some(p) => MjEventBaGang(p, evt) :: acc
        case None => acc
      }
    }
    // 是否有暗杠
    var weight = 0
    var repeat = 0
    var tmpList:List[Mj] = Nil
    for (p <- pais) {
      tmpList = p :: tmpList
      if (weight == 0 || p.weight == weight) {
        repeat += 1
        if (repeat == 4) {
          // 找到重复
          list = MjEventAnGang(tmpList) :: list
          tmpList = Nil
          repeat = 0
        }
      } else {
        weight = p.weight
        tmpList = Nil
        repeat = 0
      }
    }
    list
  }

}
