package com.wangboo.query

import java.sql.{Connection, ResultSet}

import scala.collection.TraversableOnce
import scala.collection.mutable.ListBuffer

/**
  * Created by wangboo on 2017/3/9.
  */
class CommonSQL(conn:Connection,sql:String) {

  def update(first: Any, tail: Any*):Int = update(first :: tail.toList)
  def update(args:TraversableOnce[Any]=List.empty):Int = {
    val stmt = conn.prepareStatement(sql)
    try {
      if(args.nonEmpty) {
        args.toIterable.zipWithIndex.foreach(p => stmt.setObject(p._2+1, p._1))
      }
      stmt.executeUpdate()
    }finally {
      stmt.close()
    }
  }

  def batchUpdate(first:TraversableOnce[Any], tail:TraversableOnce[Any]*):Int = batchUpdate(first :: tail.toList)
  def batchUpdate(args: List[TraversableOnce[Any]]):Int = {
    conn.setAutoCommit(false)
    val stmt = conn.prepareStatement(sql)
    try {
      for(arg <- args) {
        arg.toIterable.zipWithIndex.foreach(p => stmt.setObject(p._2 + 1, p._1))
        stmt.addBatch()
      }
      stmt.executeBatch().size
    }finally {
      stmt.close()
      conn.setAutoCommit(true)
    }
  }

  def map[A](f: Result => A):List[A] = map(List.empty, f)
  def map[A](args: TraversableOnce[Any], f: Result => A):List[A] = {
    val stmt = conn.prepareStatement(sql)
    try {
      if(args.nonEmpty) {
        args.toIterable.zipWithIndex.foreach(p => stmt.setObject(p._2+1, p._1))
      }
      val rs = Result(stmt.executeQuery())
      val list = ListBuffer[A]()
      while(rs.next) {
        list += f(rs)
      }
      list.toList
    } finally {
      stmt.close()
    }
  }

}

case class Result(rs:ResultSet) {
  def apply[T](column:String) = rs.getObject(column).asInstanceOf[T]
  private[query] def next = rs.next()
}