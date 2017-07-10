package com.wangboo.play

/**
  * Created by wangboo on 2017/6/29.
  */

class A
class B
class C

class O[T >: B](t: T)

object ExtTest {

  def main(args: Array[String]): Unit = {
    val os = None // Some("hello")
    os.foreach(str => println("str = " + str))
  }

}
