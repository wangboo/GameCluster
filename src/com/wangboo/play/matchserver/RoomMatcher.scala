package com.wangboo.play.matchserver

import com.wangboo.cluster.MQServerLike

import scala.collection.mutable

/**
  * Created by wangboo on 2017/6/20.
  */
case class MatchRole(roleId:Long, gate:String)
class RoomMatcher(roomType:Int, roomSize:Int, mq: MQServerLike) {

  val queue = mutable.Queue[MatchRole]()
  val roleIdSet = mutable.HashSet[Long]()

  def enqueue(roleId:Long, gate:String) {
    if (roleIdSet.contains(roleId)) {
      // 已经在排队了
//      val fail =
//      mq.publish(gate, )
      return
    }
    queue.enqueue(MatchRole(roleId, gate))
    if(queue.size >= roomSize) {
      val droped = (1 to roomSize).map{n => queue.dequeue()}.toList

    }
  }

}
