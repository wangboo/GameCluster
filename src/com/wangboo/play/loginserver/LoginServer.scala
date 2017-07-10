package com.wangboo.play.loginserver

import com.wangboo.cluster.protocol.CommonProto.{CommonMsg, CommonProxyStateResp}
import com.wangboo.cluster.protocol.L2cProto.{L2CMessage, L2CProxyInfoResp}
import com.wangboo.cluster.protocol.MsgIdProto.MsgId
import com.wangboo.cluster.{MQBehaviour, MQServerLike, ThreadServer, _}
import com.wangboo.play.GateServerState
import org.apache.mina.core.session.IoSession
import org.slf4j.LoggerFactory

/**
  * Created by wangboo on 2017/6/15.
  */
class LoginServer extends ThreadServer("LoginServer") with MQServerLike {
  val logger = LoggerFactory.getLogger(getClass)
  override val mq: MQBehaviour = jedisMQ(this)
  var proxyMap = Map[String, GateServerState]()

  subscribe("common") {
    // 收到网关定时上报，自身状态
    case PbMsg(MsgId.Common_ProxyStateResp, msg: CommonMsg) => handleProxyStateResp(msg.getCommonProxyStateResp)
    case msg =>
      logger.error("recv unhandled msg: {}", msg)
  }

  def handleProxyStateResp(msg: CommonProxyStateResp): Unit = {
    proxyMap.get(msg.getSubname) match {
      case Some(proxy) => proxy.update(msg)
      case None => proxyMap += msg.getSubname -> GateServerState(msg)
    }
  }

  override def handle = {
    case SessionMsg(MsgId.C2L_ProxyInfoQuery, _, session) => handleProxyInfoQuery(session)
    case msg => super.handle(msg)
  }

  def handleProxyInfoQuery(session: IoSession): Unit = {
    val info = if (proxyMap.isEmpty) {
      L2CProxyInfoResp.newBuilder().setState(1)
    } else {
      val info = proxyMap.values.minBy(_.loadRate)
      L2CProxyInfoResp.newBuilder()
        .setState(0)
        .setCount(info.connCount)
        .setIp(info.ip)
        .setPort(info.port)
    }
    val resp = L2CMessage.newBuilder()
      .setMsgId(MsgId.L2C_ProxyInfoResp)
      .setProxyInfoResp(info)
      .build()
    session.write(resp)
  }
}
