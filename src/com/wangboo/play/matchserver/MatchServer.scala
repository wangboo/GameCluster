package com.wangboo.play.matchserver

import com.wangboo.cluster.{MQBehaviour, MQServerLike, ThreadServer}
import com.wangboo.cluster._
/**
  * Created by wangboo on 2017/6/20.
  */
class MatchServer extends ThreadServer("MatchServer") with MQServerLike {
  override val mq: MQBehaviour = jedisMQ(this)

//  subscribe("match") {
//    case MQMsg("match", PbMsg()) =>
//  }

}
