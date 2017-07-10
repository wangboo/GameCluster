package com.wangboo.play.gateserver

import com.wangboo.cluster.protocol.Client2GateProto.Client2GateMsg
import com.wangboo.cluster.{MinaServer, SessionLeaveMsg, SessionMsg}
import org.apache.mina.core.service.IoHandlerAdapter
import org.apache.mina.core.session.IoSession
import org.slf4j.LoggerFactory

/**
  * Created by wangboo on 2017/6/29.
  */
object GateMinaServer {
  val logger = LoggerFactory.getLogger("GateMinaServer")
}
class GateMinaServer extends IoHandlerAdapter {

  val mina = new MinaServer[Client2GateMsg](this, Client2GateMsg.parseFrom, 9001, 1)

  def start() = mina.start()

  def shutdown() = mina.shutdown()

  override def messageReceived(session: IoSession, message: scala.Any): Unit = {
    message match {
      case c2gMsg:Client2GateMsg => GateApp.gateServer.push(SessionMsg(c2gMsg.getMsgId, c2gMsg, session))
      case _ => GateMinaServer.logger.warn("GateMinaServer recv unknown msg: {}", message)
    }
  }

  override def sessionClosed(session: IoSession): Unit = {
    GateApp.gateServer.push(SessionLeaveMsg(session))
  }

}
