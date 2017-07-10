package com.wangboo.cluster

import scala.collection.mutable
import scala.util.control.Breaks._

trait TimerManagerLike {
  def tick(): Unit
}

trait TimerManager extends TimerManagerLike {
  type Callback = ()=>Unit
  def after(after: Long = 0)(func: Callback):Long
  def interval(interval: Long, after: Long = -1)(func: Callback):Long
  def remove(id:Long)
}

class TimerTick(val id:Long, var at: Long, val func: ()=>Unit) extends Comparable[TimerTick] {
  def triggerAt = at
  override def compareTo(o: TimerTick): Int = (at - o.triggerAt).toInt
  def exec(now:Long) = func.apply()
}

class IntervalTimerTick(id:Long, at: Long, interval: Long, func: ()=>Unit) extends TimerTick(id, at, func) {
  var nextAt = at
  override def triggerAt: Long = nextAt
  override def exec(now:Long): Unit = {
    nextAt = now + interval
    func.apply()
  }
}

// not thread safe
trait WheelLikeTimerManager extends TimerManager {
  var tickSet = mutable.TreeSet[TimerTick]()
  val appending = mutable.Set[TimerTick]()
  val removing = mutable.Set[Long]()
  var lastTick = 0L
  var autoId = 0L

  override def after(after: Long = 0)(func: Callback): Long = {
    autoId += 1
    appending += new TimerTick(autoId, System.currentTimeMillis() + after, func)
    autoId
  }

  override def interval(interval: Long, after: Long = -1)(func: Callback): Long = {
    autoId += 1
    appending += (if (after == -1)
      new IntervalTimerTick(autoId, System.currentTimeMillis() + interval, interval, func)
    else
      new IntervalTimerTick(autoId, System.currentTimeMillis() + after, interval, func)
      )
    autoId
  }

  def remove(id:Long) = removing.add(id)

  override def tick(): Unit = {
    val now = System.currentTimeMillis()
    if(lastTick == 0) {
      lastTick = now
      return
    }
    // 频率控制
    if (now - lastTick < 40) return
    lastTick = now
    // 移除
    if (removing.nonEmpty) {
      tickSet = tickSet.filterNot(t => removing.contains(t.id))
      removing.clear()
    }
    // 增加
    if(appending.nonEmpty) {
      appending.foreach(tickSet.add _)
      appending.clear()
    }
    if(tickSet.isEmpty) return
    // 执行
    var remove = List[TimerTick]()
    breakable {
      for (t <- tickSet) {
        if (t.triggerAt < now) {
          t.exec(now)
          if (!t.isInstanceOf[IntervalTimerTick]) remove = t :: remove
        }
        else break
      }
    }
    // 移除
    remove.foreach(tickSet.remove _)
  }
}