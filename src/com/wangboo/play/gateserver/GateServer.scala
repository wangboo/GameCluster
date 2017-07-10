package com.wangboo.play.gateserver

import com.google.protobuf.GeneratedMessage
import com.wangboo.cluster.protocol.Client2GateProto.Client2GateMsg
import com.wangboo.cluster.protocol.CommonProto.{CommonMsg, CommonPlayerLeave, CommonProxyStateResp}
import com.wangboo.cluster.protocol.Gate2ClientProto.{G2C_LoginResp, G2C_Talk, Gate2ClientMsg}
import com.wangboo.cluster.protocol.MsgIdProto.MsgId
import com.wangboo.cluster.{MQBehaviour, MQServerLike, ThreadServer, _}
import org.apache.mina.core.session.IoSession
import org.slf4j.LoggerFactory

/**
  * 网关服务 serverName = GateServer_1, subName = gate.1
  * Created by wangboo on 2017/6/15.
  */
class GateServer(serverName: String, subName: String) extends ThreadServer(serverName) with MQServerLike with WheelLikeTimerManager {

  val logger = LoggerFactory.getLogger(getClass)
  override val mq: MQBehaviour = jedisMQ(this)
  // 订阅该网关信息
  subscribe(subName) {
    case PbMsg(MsgId.Common_talk, pb: CommonMsg) => // 通用聊天广播
      val msg = Gate2ClientMsg.newBuilder()
        .setMsgId(MsgId.Gate2Client_Talk)
        .setTalk(G2C_Talk.newBuilder().setFromId(pb.getCommontalk.getFromId).setFromName(pb.getCommontalk.getFormName).setMsg(pb.getCommontalk.getMsg))
        .build()
      pb.getCommontalk.getUserIdList.forEach(id => GateSessionMgr.get(id).foreach(_.write(msg)))
    case PbMsg(MsgId.Common_g2cMsg, pb: CommonMsg) => // 内部消息转发给客户端
      if (pb.hasFromUserId) GateSessionMgr.get(pb.getFromUserId).foreach(_.write(pb.getG2Cmsg))
    case msg => logger.warn("unhandled msg on {}, {}", subName, msg)
  }
  // 订阅通用信息
  subscribe("common") {
    case PbMsg(MsgId.Common_HeartBeat, pb: CommonMsg) =>
      publish(pb.getFrom, CommonMsg.newBuilder().setMsgId(MsgId.Common_HeartBeatResp).setFrom(subName).setAt(System.currentTimeMillis()).build())
    case msg => logger.warn("unhandled msg on common, {}", msg)
  }
  // 每隔固定时间，定时上报网关状态
  interval(5000) { () =>
    val config = GateApp.gateConfig
    val msg = CommonMsg.newBuilder().setMsgId(MsgId.Common_ProxyStateResp).setFrom(subName)
      .setCommonProxyStateResp(
        CommonProxyStateResp.newBuilder().setConnCount(GateSessionMgr.size).setConnLimit(config.maxConn).setQueueCount(queueSize)
          .setIp(config.ip).setPort(config.port).setSubname(subName)).setAt(System.currentTimeMillis()).build()
    publish("login", msg)
  }

  override def handle = {
    case SessionMsg(msgId, pb, session) => handleSessionMsg(msgId, pb, session)
    case SessionLeaveMsg(session) => handleSessionLeave(session)
    case msg => super.handle(msg)
  }

  def handleSessionMsg[Pb >: GeneratedMessage](msgId: MsgId, pb: Pb, session: IoSession) = {
    pb match {
      case c2gMsg: Client2GateMsg => handleClient2GateMsg(msgId, c2gMsg, session)
      case _ => None
    }
  }

  private def handleClient2GateMsg(msgId: MsgId, c2gMsg: Client2GateMsg, session: IoSession): Unit = {
    msgId match {
      case MsgId.Client2Gate_Login =>
        GateApp.dbServer.callbackFunc(this, () => GateApp.dbServer.login(c2gMsg.getLoginMsg), (result: Int) => handleLoginQueryResult(result, session))
      case _ => logger.error("GateServer recv unhandled msg: {}", c2gMsg)
    }
  }

  def handleSessionLeave(session: IoSession): Unit = {
    val op = IoSessionUtil.getPlayer(session)
    if (op.isEmpty) return // 还没登录
    val player = op.get
    GateSessionMgr.remove(player.userId)
    // 如果在游戏中，通知游戏，有玩家离线
    val os = GamePlayerStateMgr.remove(player.userId)
    if (os.isDefined) {
      val state = os.get
      val msg = CommonMsg.newBuilder()
        .setMsgId(MsgId.Common_PlayerLeave)
        .setFrom(subName)
        .setAt(System.currentTimeMillis())
        .setCommonPlayerLeave(CommonPlayerLeave.newBuilder()
          .setUserId(player.userId)
          .setTable(state.table))
        .build()
      publish(state.game, msg)
    }
  }

  private def handleLoginQueryResult(result: Int, session: IoSession): Unit = {
    val resp = if (result == 0) {
      // 登录成功
      val token = Math.random().toString
      IoSessionUtil.setToken(session, token)
      Gate2ClientMsg.newBuilder()
        .setMsgId(MsgId.Gate2Client_LoginResp)
        .setResult(0)
        .setLoginResp(G2C_LoginResp.newBuilder().setToken(token))
        .build()
    } else {
      Gate2ClientMsg.newBuilder()
        .setMsgId(MsgId.Gate2Client_LoginResp)
        .setResult(result)
        .build()
    }
    session.write(resp)
  }

}
