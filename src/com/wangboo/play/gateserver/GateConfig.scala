package com.wangboo.play.gateserver

import com.typesafe.config.Config

/**
  * Created by wangboo on 2017/6/29.
  */
case class GateConfig(config:Config) {
  val subName = config.getString("subname")
  val ip = config.getString("ip")
  val port = config.getInt("port")
  val maxConn = config.getInt("maxconn")
  val serverName = config.getString("serverName")
  //// ----- db ------
  val dbServerName = config.getString("db.serverName")
  // 数据库连接
  val dbUrl = config.getString("db.url")
  // 心跳检查
  val dbHeartbeat = config.getInt("db.heartbeat") * 1000

  val dbDriver = config.getString("db.driver")
}