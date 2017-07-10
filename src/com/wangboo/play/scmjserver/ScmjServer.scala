package com.wangboo.play.scmjserver

import com.wangboo.cluster.{MQBehaviour, MQServerLike, ThreadServer}
import com.wangboo.cluster._

import scala.collection.mutable
/**
  *
  * Created by wangboo on 2017/6/20.
  */
class ScmjServer(subName:String, tableLimit:Int) extends ThreadServer("ScmjServer") with MQServerLike with WheelLikeTimerManager {
  override val mq: MQBehaviour = jedisMQ(this)

  val tableMap = mutable.Map[Int, ScmjTable]()

  interval(5000){() =>
    println("hello world")
  }

  after(1000) { () =>
    println("after 1000")
  }

  subscribe(subName) {
    case _ => println("")
  }

}
