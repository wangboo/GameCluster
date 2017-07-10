package com.wangboo.cluster

import com.google.protobuf.GeneratedMessage
import com.wangboo.cluster.protocol.MsgIdProto.MsgId
import org.apache.mina.core.session.IoSession

/**
  * Msg
  * Created by wangboo on 2017/6/13.
  */
trait Msg

trait FuncMsg extends Msg {
  def apply(): Unit
}

case class ObjMsg[T](msgId: String, t: T) extends Msg {
  override def toString: String = s"ObjMsg: $msgId, obj: $t"
}

case class SessionMsg[Pb >: GeneratedMessage](msgId: MsgId, pb: Pb, session:IoSession) extends Msg
case class SessionLeaveMsg(session:IoSession) extends Msg

class ApplyMsg(func: () => Unit) extends FuncMsg {
  override def apply(): Unit = func.apply()
}

class Apply1Msg[T](t: T, func: (T) => Unit) extends FuncMsg {
  override def apply(): Unit = func.apply(t)
}

class CallbackMsg[T](cbServer: Server, calc: () => T, applyFunc: (T) => Unit) extends FuncMsg {
  override def apply(): Unit = {
    val t = calc.apply()
    val applyMsg = new Apply1Msg(t, applyFunc)
    cbServer.push(applyMsg)
  }
}


