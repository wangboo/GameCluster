package com.wangboo.query

import java.sql.ResultSet

import scala.collection.immutable.HashMap

/**
  * Created by wangboo on 2017/2/22.
  */
case class MapHandler() extends QueryHandler[Map[String, AnyVal]] {
  override def handle(rs: ResultSet): Map[String, AnyVal] = {
    val count = rs.getMetaData.getColumnCount
    var map = HashMap[String, AnyVal]()
    for (i <- 1 to count) {
      val name = rs.getMetaData.getColumnLabel(i)
      val obj = rs.getObject(i)
      map += (name -> obj.asInstanceOf[AnyVal])
    }
    map
  }
}

object MapHandler {
  val instance = MapHandler()
}

case class FunctionalQueryHandler[T](handler: ResultSet => T) extends QueryHandler[T] {
  override def handle(rs: ResultSet): T = handler(rs)
}

case class XQ1[T1, R](h: T1 => R) extends QueryHandler[R] {
  override def handle(rs: ResultSet): R = {
    val a1 = rs.getObject(1).asInstanceOf[T1]
    h(a1)
  }
}
case class XQ2[T1, T2, R](h: (T1, T2) => R) extends QueryHandler[R] {
  override def handle(rs: ResultSet): R = {
    val a1 = rs.getObject(1).asInstanceOf[T1]
    val a2 = rs.getObject(2).asInstanceOf[T2]
    h(a1,a2)
  }
}
case class XQ3[T1, T2, T3, R](h: (T1, T2, T3) => R) extends QueryHandler[R] {
  override def handle(rs: ResultSet): R = {
    val a1 = rs.getObject(1).asInstanceOf[T1]
    val a2 = rs.getObject(2).asInstanceOf[T2]
    val a3 = rs.getObject(3).asInstanceOf[T3]
    h(a1,a2,a3)
  }
}
case class XQ4[T1,T2,T3,T4, R](h: (T1,T2,T3,T4) => R) extends QueryHandler[R] {
  override def handle(rs: ResultSet): R = {
    val a1 = rs.getObject(1).asInstanceOf[T1]
    val a2 = rs.getObject(2).asInstanceOf[T2]
    val a3 = rs.getObject(3).asInstanceOf[T3]
    val a4 = rs.getObject(4).asInstanceOf[T4]
    h(a1,a2,a3,a4)
  }
}
case class XQ5[T1,T2,T3,T4,T5, R](h: (T1,T2,T3,T4,T5) => R) extends QueryHandler[R] {
  override def handle(rs: ResultSet): R = {
    val a1 = rs.getObject(1).asInstanceOf[T1]
    val a2 = rs.getObject(2).asInstanceOf[T2]
    val a3 = rs.getObject(3).asInstanceOf[T3]
    val a4 = rs.getObject(4).asInstanceOf[T4]
    val a5 = rs.getObject(5).asInstanceOf[T5]
    h(a1,a2,a3,a4,a5)
  }
}
case class XQ6[T1,T2,T3,T4,T5,T6, R](h: (T1,T2,T3,T4,T5,T6) => R) extends QueryHandler[R] {
  override def handle(rs: ResultSet): R = {
    val a1 = rs.getObject(1).asInstanceOf[T1]
    val a2 = rs.getObject(2).asInstanceOf[T2]
    val a3 = rs.getObject(3).asInstanceOf[T3]
    val a4 = rs.getObject(4).asInstanceOf[T4]
    val a5 = rs.getObject(5).asInstanceOf[T5]
    val a6 = rs.getObject(6).asInstanceOf[T6]
    h(a1,a2,a3,a4,a5,a6)
  }
}
case class XQ7[T1,T2,T3,T4,T5,T6,T7, R](h: (T1,T2,T3,T4,T5,T6,T7) => R) extends QueryHandler[R] {
  override def handle(rs: ResultSet): R = {
    val a1 = rs.getObject(1).asInstanceOf[T1]
    val a2 = rs.getObject(2).asInstanceOf[T2]
    val a3 = rs.getObject(3).asInstanceOf[T3]
    val a4 = rs.getObject(4).asInstanceOf[T4]
    val a5 = rs.getObject(5).asInstanceOf[T5]
    val a6 = rs.getObject(6).asInstanceOf[T6]
    val a7 = rs.getObject(7).asInstanceOf[T7]
    h(a1,a2,a3,a4,a5,a6,a7)
  }
}
case class XQ8[T1,T2,T3,T4,T5,T6,T7,T8, R](h: (T1,T2,T3,T4,T5,T6,T7,T8) => R) extends QueryHandler[R] {
  override def handle(rs: ResultSet): R = {
    val a1 = rs.getObject(1).asInstanceOf[T1]
    val a2 = rs.getObject(2).asInstanceOf[T2]
    val a3 = rs.getObject(3).asInstanceOf[T3]
    val a4 = rs.getObject(4).asInstanceOf[T4]
    val a5 = rs.getObject(5).asInstanceOf[T5]
    val a6 = rs.getObject(6).asInstanceOf[T6]
    val a7 = rs.getObject(7).asInstanceOf[T7]
    val a8 = rs.getObject(8).asInstanceOf[T8]
    h(a1,a2,a3,a4,a5,a6,a7,a8)
  }
}
case class XQ9[T1,T2,T3,T4,T5,T6,T7,T8,T9, R](h: (T1,T2,T3,T4,T5,T6,T7,T8,T9) => R) extends QueryHandler[R] {
  override def handle(rs: ResultSet): R = {
    val a1 = rs.getObject(1).asInstanceOf[T1]
    val a2 = rs.getObject(2).asInstanceOf[T2]
    val a3 = rs.getObject(3).asInstanceOf[T3]
    val a4 = rs.getObject(4).asInstanceOf[T4]
    val a5 = rs.getObject(5).asInstanceOf[T5]
    val a6 = rs.getObject(6).asInstanceOf[T6]
    val a7 = rs.getObject(7).asInstanceOf[T7]
    val a8 = rs.getObject(8).asInstanceOf[T8]
    val a9 = rs.getObject(9).asInstanceOf[T9]
    h(a1,a2,a3,a4,a5,a6,a7,a8,a9)
  }
}
case class XQ10[T1,T2,T3,T4,T5,T6,T7,T8,T9,T10, R](h: (T1,T2,T3,T4,T5,T6,T7,T8,T9,T10) => R) extends QueryHandler[R] {
  override def handle(rs: ResultSet): R = {
    val a1 = rs.getObject(1).asInstanceOf[T1]
    val a2 = rs.getObject(2).asInstanceOf[T2]
    val a3 = rs.getObject(3).asInstanceOf[T3]
    val a4 = rs.getObject(4).asInstanceOf[T4]
    val a5 = rs.getObject(5).asInstanceOf[T5]
    val a6 = rs.getObject(6).asInstanceOf[T6]
    val a7 = rs.getObject(7).asInstanceOf[T7]
    val a8 = rs.getObject(8).asInstanceOf[T8]
    val a9 = rs.getObject(9).asInstanceOf[T9]
    val a10 = rs.getObject(10).asInstanceOf[T10]
    h(a1,a2,a3,a4,a5,a6,a7,a8,a9,a10)
  }
}
case class XQ11[T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11, R](h: (T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11) => R) extends QueryHandler[R] {
  override def handle(rs: ResultSet): R = {
    val a1 = rs.getObject(1).asInstanceOf[T1]
    val a2 = rs.getObject(2).asInstanceOf[T2]
    val a3 = rs.getObject(3).asInstanceOf[T3]
    val a4 = rs.getObject(4).asInstanceOf[T4]
    val a5 = rs.getObject(5).asInstanceOf[T5]
    val a6 = rs.getObject(6).asInstanceOf[T6]
    val a7 = rs.getObject(7).asInstanceOf[T7]
    val a8 = rs.getObject(8).asInstanceOf[T8]
    val a9 = rs.getObject(9).asInstanceOf[T9]
    val a10 = rs.getObject(10).asInstanceOf[T10]
    val a11 = rs.getObject(11).asInstanceOf[T11]
    h(a1,a2,a3,a4,a5,a6,a7,a8,a9,a10,a11)
  }
}
case class XQ12[T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12, R](h: (T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12) => R) extends QueryHandler[R] {
  override def handle(rs: ResultSet): R = {
    val a1 = rs.getObject(1).asInstanceOf[T1]
    val a2 = rs.getObject(2).asInstanceOf[T2]
    val a3 = rs.getObject(3).asInstanceOf[T3]
    val a4 = rs.getObject(4).asInstanceOf[T4]
    val a5 = rs.getObject(5).asInstanceOf[T5]
    val a6 = rs.getObject(6).asInstanceOf[T6]
    val a7 = rs.getObject(7).asInstanceOf[T7]
    val a8 = rs.getObject(8).asInstanceOf[T8]
    val a9 = rs.getObject(9).asInstanceOf[T9]
    val a10 = rs.getObject(10).asInstanceOf[T10]
    val a11 = rs.getObject(11).asInstanceOf[T11]
    val a12 = rs.getObject(12).asInstanceOf[T12]
    h(a1,a2,a3,a4,a5,a6,a7,a8,a9,a10,a11,a12)
  }
}