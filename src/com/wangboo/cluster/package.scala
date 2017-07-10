package com.wangboo

import com.google.protobuf.GeneratedMessage
import com.wangboo.cluster.protocol.CommonProto.CommonMsg
import com.wangboo.cluster.protocol.Proto.P2CenterMsg

/**
  * Created by wangboo on 2017/6/15.
  */
package object cluster {
  val centerDecode = (bin:Array[Byte]) => {
    val pb = P2CenterMsg.parseFrom(bin)
    PbMsg(pb.getMsgId, pb)
  }
  val commonDecode = (bin:Array[Byte]) => {
    val pb = CommonMsg.parseFrom(bin)
    PbMsg(pb.getMsgId, pb)
  }
  val defaultDecodeMap: Map[String, (Array[Byte]) => PbMsg[GeneratedMessage]] = Map(
    "center" -> centerDecode,
    "common" -> commonDecode
  )
  def jedisMQ(server: MQServerLike) = {
    new JedisMQ(server, "127.0.0.1", 6379) {
      override val decodeMap: Map[String, (Array[Byte]) => PbMsg[GeneratedMessage]] = defaultDecodeMap
    }
  }

}
