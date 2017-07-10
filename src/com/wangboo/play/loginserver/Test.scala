package com.wangboo.play.loginserver

import scala.collection.mutable

/**
  * Created by wangboo on 2017/6/15.
  */



object Test {

  def main(args: Array[String]): Unit = {
    val queue = mutable.Queue[Int]()
    (1 to 10).foreach{n => queue += n}
//    val droped = queue.drop(4)
//    println(droped)
    println(queue.dequeue())
    println(queue.dequeue())
    println(queue.dequeue())
    println(queue)
  }

}
