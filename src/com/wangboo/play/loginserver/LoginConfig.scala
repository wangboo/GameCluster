package com.wangboo.play.loginserver

import com.typesafe.config.Config

/**
  * Created by wangboo on 2017/6/29.
  */
class LoginConfig(config:Config) {

  val port = config.getInt("port")
  val processCount = config.getInt("processCount")

}
