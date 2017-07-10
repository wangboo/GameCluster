package com.wangboo.cluster

import java.util.concurrent.{LinkedBlockingQueue, TimeUnit}

/**
  * Server
  * Created by wangboo on 2017/6/13.
  */
trait Server extends TimerManagerLike {
  type Matcher = PartialFunction[Msg, Unit]

  def name(): String

  def start(): Boolean

  def onStart()

  def push(msg: Msg)

  def handle: Matcher = {
    case _ => println("msg not handled in trait Server")
  }

  def shutdown(): Boolean

  def onShutdown()

  def isRunning: Boolean

  override def tick(): Unit = None
}

/**
  * 函数式的服务器
  */
trait FuncServerLike extends Server {
  def applyFunc(func: () => Unit) = push(new ApplyMsg(func))

  def apply1Func[T](t: T, func1: (T) => Unit) = push(new Apply1Msg[T](t, func1))

  def callbackFunc[T](cbServer: Server, calc: () => T, applyFunc: (T) => Unit) = push(new CallbackMsg[T](cbServer, calc, applyFunc))
}

/**
  * 基于消息的服务器
  */
trait ObjServerLike extends Server {
  def pushObj[T](msgId: String, t: T) = push(ObjMsg(msgId, t))
}

abstract class ThreadServer(serverName: String) extends Server with Runnable {

  private val thread = new Thread(this)
  private val msgQueue = new LinkedBlockingQueue[Msg]()
  private var started = false
  private var running = true
  private var tryingShutdown = false

  override def name(): String = serverName

  override def start(): Boolean = {
    if (!started) {
      started = true;
      thread.start()
    }
    !started
  }

  override def isRunning: Boolean = running

  override def run(): Unit = {
    onStart()
    while (running) {
      val msg = msgQueue.poll(100, TimeUnit.MILLISECONDS)
      try {
        if (msg != null) {
          msg match {
            case msg: FuncMsg => msg.apply()
            case _ => handle(msg)
          }
        } else if (tryingShutdown) {
          running = false
        }
      } catch {
        case e: Throwable => e.printStackTrace()
      } finally {
        tick()
      }
    }
    onShutdown()
  }

  override def push(msg: Msg): Unit = msgQueue.put(msg)

  override def shutdown(): Boolean = {
    if (started && running) tryingShutdown = true
    started && running
  }

  override def onStart(): Unit = None

  override def onShutdown(): Unit = None

  def queueSize:Int = msgQueue.size()

}
