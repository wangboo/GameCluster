package com.wangboo.play.loginserver

import com.typesafe.config.ConfigFactory
import com.wangboo.cluster.{Msg, ObjMsg}

/**
  * Created by wangboo on 2017/6/15.
  */
object LoginApp {

  val config = ConfigFactory.parseResources("login.conf")
  val loginConfig = new LoginConfig(config.getConfig("login"))
  val loginServer = new LoginServer()
  val minaServer = new LoginMinaServer()

  def push(msgId:String, t: Any) = loginServer.push(ObjMsg(msgId, t))
  def push(msg:Msg) = loginServer.push(msg)

  def main(args: Array[String]): Unit = {
    loginServer.start()
    minaServer.start()
  }

}
