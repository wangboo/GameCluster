package com.wangboo.play.gateserver

import java.sql.{Connection, DriverManager}

import com.wangboo.cluster.protocol.Client2GateProto.C2G_LoginMsg
import com.wangboo.cluster.{FuncServerLike, ThreadServer}
import com.wangboo.query.{MapHandler, Query}

/**
  * Created by wangboo on 2017/6/29.
  */
class GateDbServer(serverName:String) extends ThreadServer(serverName) with FuncServerLike {

  private var conn:Connection = _

  def getConn:Connection = {
    if(conn == null) {
      Class.forName(GateApp.gateConfig.dbDriver)
    }
    if (conn == null || !conn.isValid(500) || conn.isClosed) {
      conn = DriverManager.getConnection(GateApp.gateConfig.dbUrl)
    }
    conn
  }

  def login(req:C2G_LoginMsg): Int ={
    val q = Query(getConn, "user", MapHandler.instance)
    q.select("password")
      .where("useranme = ? and channel = ?", req.getUsername, req.getChannel)
      .one match {
      case Some(data) =>
        if(data("password").asInstanceOf[String].equals(req.getPassword)) 0 else 1
      case None => 2
    }
  }

}
