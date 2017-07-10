package com.wangboo.query

import java.sql.{Connection, ResultSet}

/**
  * Created by wangboo on 2017/2/23.
  */
trait QueryHandler[T] {
  def handle(rs: ResultSet):T
}

trait XqHandler[T] {
  def tableName:String
  def xq: QueryHandler[T]
  def q(implicit conn:Connection) = Query(conn, tableName, xq)
}
