package com.wangboo.play.gateserver

import org.apache.mina.core.session.IoSession

/**
  * Created by wangboo on 2017/6/29.
  */
object IoSessionUtil {

  val keyGamePlayer = "GamePlayer"
  val keyToken = "token"

  def getPlayer(session:IoSession):Option[GamePlayer] = {
    val player = session.getAttribute(keyGamePlayer)
    if(player == null) None else Some(player.asInstanceOf[GamePlayer])
  }
  def setPlayer(session:IoSession, player:GamePlayer) = session.setAttribute(keyGamePlayer, player)

  def setToken(session: IoSession, token:String) = session.setAttribute(keyToken, token)

}
