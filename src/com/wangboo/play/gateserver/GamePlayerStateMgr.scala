package com.wangboo.play.gateserver

import scala.collection.mutable

/**
  * Created by wangboo on 2017/6/29.
  */
case class GamePlayerState(gamePlayer: GamePlayer, game:String, table:Int)
object GamePlayerStateMgr {

  val map = mutable.Map[Long, GamePlayerState]()

  def put(state:GamePlayerState):Unit = {
    map.put(state.gamePlayer.userId, state)
  }

  def remove(id:Long):Option[GamePlayerState] = map.remove(id)

  def get(id:Long):Option[GamePlayerState] = map.get(id)

}
