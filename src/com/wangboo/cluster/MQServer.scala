package com.wangboo.cluster

import com.google.protobuf.GeneratedMessage
import com.wangboo.cluster.protocol.MsgIdProto.MsgId
import redis.clients.jedis.{BinaryJedisPubSub, Client}

/**
  * Created by wangboo on 2017/6/14.
  */

case class MQMsg(channel:String, pb: PbMsg[_ <: GeneratedMessage]) extends Msg
case class PbMsg[+Pb <: GeneratedMessage](msgId: MsgId, t: Pb) extends Msg

trait MQBehaviour {
  type Matcher = PartialFunction[Any, Unit]

  def start()

  def isRunning:Boolean

  def shutdown()

  def channels:List[String]

  def subscribe(channels: String*)

  def unSubscribe(channels: String*)

  def publish(channel: String, pb: GeneratedMessage):Long

  // channel -> PartialFunction
  val decodeMap: Map[String, (Array[Byte]) => PbMsg[GeneratedMessage]]
}

abstract class JedisMQ(server: MQServerLike, ip: String, port: Int) extends BinaryJedisPubSub with MQBehaviour with Runnable {
  private val subCli = new Client(ip, port)
  private val pubCli = new Client(ip, port)
  private var subThread:Thread = _
  private var running = false
  subCli.setTimeoutInfinite()
//  override val decodeMap: Map[String, (Array[Byte]) => PbMsg[GeneratedMessage]]

  override def start(): Unit = {
    if (!running) {
      subThread = new Thread(this)
      subThread.start()
    }
  }

  override def isRunning: Boolean = running
  override def channels: List[String] = server.matcherMap.keys.toList
  def channelsByteArray = channels.map{_.getBytes(Util.charset)}

  override def shutdown(): Unit = {
    if(running) {
      subCli.unsubscribe(channels:_*)
      running = false
    }
  }

  override def run(): Unit = {
    println(s"MQClient run, sub channels: $channels")
    proceed(subCli, channelsByteArray:_*)
    running = false
  }

  override def subscribe(channels: String*): Unit = {
    subCli.subscribe(channels: _*)
  }

  override def unSubscribe(channels: String*): Unit = subCli.unsubscribe(channels: _*)

  override def publish(channel: String, pb: GeneratedMessage): Long = {
    pubCli.publish(channel.getBytes(Util.charset), pb.toByteArray)
    pubCli.getIntegerReply
  }

  override def onMessage(channel: Array[Byte], data: Array[Byte]): Unit = {
    val ch = new String(channel, Util.charset)
    val coder = decodeMap.get(ch)
    if (coder.isDefined) {
      try {
        val msg = coder.get.apply(data)
        server.push(MQMsg(ch, msg))
      } catch {
        case e: Throwable => e.printStackTrace()
      }
    } else {
      println(s"warning! channel: $ch has not decoder")
    }
  }
}

trait MQServerLike extends Server {

  val mq: MQBehaviour
  var matcherMap = Map[String, Matcher]()
  val defaultMqMsgMatcher = (msg: MQMsg) => println(s"receive unhandled defaultMqMsgMatcher: $msg")

  def subscribe(channel: String)(matcher: Matcher) = {
    matcherMap += (channel -> matcher)
    if (!isRunning) mq.subscribe(channel)
  }

  def unSubscribe(channel: String): Unit = {
    matcherMap -= channel
    if (!isRunning) mq.unSubscribe(channel)
  }

  def publish(channel: String, pb: GeneratedMessage): Unit = {
    mq.publish(channel, pb)
  }

  override def handle = {
    case msg: MQMsg =>
      matcherMap.get(msg.channel) match {
        case Some(ap) => ap(msg.pb)
        case None => defaultMqMsgMatcher(msg)
      }
    case msg => super.handle.apply(msg)
  }

  override def onStart(): Unit = mq.start()

  override def onShutdown(): Unit = mq.shutdown()


}
