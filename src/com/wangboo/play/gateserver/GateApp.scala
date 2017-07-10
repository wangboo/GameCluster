package com.wangboo.play.gateserver

import com.typesafe.config.ConfigFactory

/**
  * Created by wangboo on 2017/6/29.
  */
object GateApp {

  private val config = ConfigFactory.parseResources("gate1.conf")
  val gateConfig = GateConfig(config.getConfig("gate"))

  val gateServer = new GateServer(gateConfig.serverName, gateConfig.subName)
  val minaServer = new GateMinaServer()
  val dbServer = new GateDbServer(gateConfig.dbServerName)

  def main(args: Array[String]): Unit = {
    gateServer.start()
    minaServer.start()
  }

}
