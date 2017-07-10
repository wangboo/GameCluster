package com.wangboo.play.gateserver

import com.wangboo.cluster.protocol.Gate2ClientProto.Gate2ClientMsg
import org.apache.mina.core.session.IoSession

import scala.collection.mutable

/**
  * Created by wangboo on 2017/6/29.
  */
object GateSessionMgr {

  // map[玩家id,IoSession]
  val map = mutable.Map[Long, IoSession]()

  def put(id:Long, session:IoSession):Unit = map.update(id, session)

  def get(id:Long):Option[IoSession] = map.get(id)
  def getGamePlayer(id:Long):Option[GamePlayer] = map.get(id) match {
    case Some(session) => IoSessionUtil.getPlayer(session)
    case None => None
  }

  def broadcast(msg:Gate2ClientMsg, to:Seq[Long]):Unit = {
    to.view.map(get).filter(_.isDefined).map(_.get).foreach(_.write(msg))
  }

  def send(msg:Gate2ClientMsg, to:Long):Boolean = {
    get(to) match {
      case Some(session) =>
        session.write(msg)
        true
      case None =>
        false
    }
  }

  def size:Int = map.size
  def remove(id:Long) = map.remove(id)

}
