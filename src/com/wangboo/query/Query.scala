package com.wangboo.query

import java.sql.{Connection, ResultSet}

import scala.collection.mutable.ListBuffer

/**
  * Created by wangboo on 2017/2/23.
  */
case class Query[T](conn:Connection, table:String, handler: QueryHandler[T]) {

  var where = ""
  var args:Array[Any] = Array.empty
  var limit:Option[Int] = None
  var order:Option[String] = None
  var select:String = "*"

  def where(sql: String, args: Any*): this.type = {
    where = sql
    this.args = args.toArray
    this
  }

  def select(list:Seq[String]):this.type = {
    select(list.mkString(","))
    this
  }

  def select(select:String):this.type = {
    this.select = select
    this
  }

  def limit(n: Int):this.type = {
    limit = Some(n)
    this
  }

  def order(order:String):this.type = {
    this.order = Some(order)
    this
  }

  def one:Option[T] = limit(1).queryOne(mkSql, handler)
  def all: Seq[T] = queryAll(mkSql, handler)

  def mkSql:String = {
    val sb = new StringBuilder(s"select $select from $table")
    if(!where.isEmpty) {
      sb.append(" where ")
      sb.append(where)
    }
    if(order.isDefined ){
      sb.append(" order by ")
      sb.append(order.get)
    }
    if (limit.isDefined) {
      sb.append(" limit ")
      sb.append(limit.get)
    }
    println("sql: " + sb)
    sb.toString()
  }

  def queryOne(sql:String, handler: QueryHandler[T]):Option[T] = {
    val stmt = conn.prepareStatement(sql)
    try {
      if(args.length > 0) args.zipWithIndex.foreach(p => stmt.setObject(p._2+1, p._1))
      val rs = stmt.executeQuery()
      if(rs.next()) {
        Some(handler.handle(rs))
      } else {
        None
      }
    } finally {
      stmt.close()
    }
  }

  def queryAll(sql:String, handler: QueryHandler[T]):Seq[T] = {
    val stmt = conn.prepareStatement(sql)
    try {
      if(args.length > 0) args.zipWithIndex.foreach(p => stmt.setObject(p._2+1, p._1))
      val buffer = ListBuffer[T]()
      val rs = stmt.executeQuery()
      while(rs.next()) {
        buffer += handler.handle(rs)
      }
      buffer.toList
    }finally {
      stmt.close()
    }
  }
}
