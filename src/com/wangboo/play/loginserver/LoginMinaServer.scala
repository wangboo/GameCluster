package com.wangboo.play.loginserver

import com.wangboo.cluster._
import com.wangboo.cluster.protocol.L2cProto.{C2LMessage, L2CMessage}
import com.wangboo.cluster.protocol.MsgIdProto.MsgId
import org.apache.mina.core.service.IoHandler
import org.apache.mina.core.session.{IdleStatus, IoSession}
import org.slf4j.LoggerFactory

/**
  * Created by wangboo on 2017/6/15.
  */
class LoginMinaServer extends IoHandler {

  val logger = LoggerFactory.getLogger(getClass)
  val mina = new MinaServer[C2LMessage](this, C2LMessage.parseFrom, LoginApp.loginConfig.port, LoginApp.loginConfig.processCount)
  val heartBeat = L2CMessage.newBuilder().setMsgId(MsgId.L2C_HeartBeat).build()

  logger.debug("server bind at: {}, processor: {}", LoginApp.loginConfig.port, LoginApp.loginConfig.processCount)

  def start() = mina.start()

  def shutdown() = mina.shutdown()

  override def exceptionCaught(session: IoSession, cause: Throwable): Unit = cause.printStackTrace()

  override def sessionIdle(session: IoSession, status: IdleStatus): Unit = {
    if (status == IdleStatus.WRITER_IDLE) session.write(heartBeat)
  }

  override def messageReceived(session: IoSession, message: scala.Any): Unit = {
    message match {
      case pb: C2LMessage =>
        val msg = SessionMsg(pb.getMsgId, pb, session)
        LoginApp.push(msg)
      case other => logger.error(s"LoginMinaServer recv unknown msg: $other")
    }
  }

  override def messageSent(session: IoSession, message: scala.Any): Unit = None

  override def sessionClosed(session: IoSession): Unit = LoginApp.push("sessionClosed", session)

  override def sessionCreated(session: IoSession): Unit = None

  override def sessionOpened(session: IoSession): Unit = LoginApp.push("sessionOpened", session)
}

