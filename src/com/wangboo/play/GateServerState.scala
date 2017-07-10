package com.wangboo.play

import com.wangboo.cluster.protocol.CommonProto.{CommonMsg, CommonProxyStateResp}
import com.wangboo.cluster.protocol.MsgIdProto.MsgId

/**
  * Created by wangboo on 2017/6/16.
  */
case class GateServerState(var connCount: Int, var queueCount: Int, ip: String, port: Int,
                           subName: String, var connLimit: Int, var lastTick:Long=0L) {
  def update(msg: CommonProxyStateResp): Unit = {
    connCount = msg.getConnCount
    queueCount = msg.getQueueCount
    connLimit = msg.getConnLimit
  }

  def loadRate: Float = (connCount + 0f) / connLimit

  def toCommonProxyStateResp = {
    CommonMsg.newBuilder().setMsgId(MsgId.Common_ProxyStateResp).setFrom(subName)
      .setCommonProxyStateResp(
        CommonProxyStateResp.newBuilder().setConnCount(connCount).setConnLimit(connLimit).setQueueCount(queueCount)
          .setIp(ip).setPort(port).setSubname(subName)).setAt(System.currentTimeMillis()).build()
  }
}

object GateServerState {
  def apply(connLimit: Int, ip: String, port: Int, subName: String):GateServerState = {
    GateServerState(0, 0, ip, port, subName, connLimit)
  }

  def apply(msg: CommonProxyStateResp): GateServerState = {
    GateServerState(msg.getConnCount, msg.getQueueCount, msg.getIp, msg.getPort, msg.getSubname, msg.getConnLimit,
      System.currentTimeMillis())
  }
}
