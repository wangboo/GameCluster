package com.wangboo

import java.sql.Connection

/**
  * Created by wangboo on 2017/3/9.
  */
package object query {

  def SQL(sql:String)(implicit conn:Connection) = new CommonSQL(conn, sql)

}
