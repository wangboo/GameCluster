package com.wangboo.play.gateserver

import org.apache.mina.core.session.IoSession

/**
  * Created by wangboo on 2017/6/29.
  */
case class GamePlayer(session:IoSession, userId:Long, name:String, gold:Int)
