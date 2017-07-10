package com.wangboo.play

import java.net.{URI, URL}
import java.util

import com.wangboo.cluster.Util
import org.apache.http.NameValuePair
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.{HttpGet, HttpPut}
import org.apache.http.impl.client.HttpClients
import org.apache.http.message.BasicNameValuePair
import spray.json.{JsArray, JsNumber, JsObject, JsString, JsValue, JsonParser, RootJsonFormat}

case class GateServerInfo(name: String, id: Int, ip: String, port: Int, cur: Int, max: Int)

object GateServerInfo extends RootJsonFormat[GateServerInfo] {
  override def write(obj: GateServerInfo): JsValue = {
    JsObject("name" -> JsString(obj.name), "id" -> JsNumber(obj.id), "ip" -> JsString(obj.ip),
      "port" -> JsNumber(obj.port), "cur" -> JsNumber(obj.cur), "max" -> JsNumber(obj.max))
  }

  override def read(json: JsValue): GateServerInfo = {
    json.asJsObject.getFields("name", "id", "ip", "port", "cur", "max") match {
      case Seq(JsString(name), JsNumber(id), JsString(ip), JsNumber(port), JsNumber(cur), JsNumber(max)) =>
        new GateServerInfo(name, id.toInt, ip, port.toInt, cur.toInt, max.toInt)
      case _ => throw new IllegalArgumentException(s"to GateServerInfo error, json = $json")
    }
  }
}

/**
  * Created by wangboo on 2017/6/20.
  */
object Test {

  def main(args: Array[String]): Unit = {
    //    val all = Mj.create108()
    //    all.foreach(println)
    //    println(all.size)
    //    val h0 :: h1 :: h2 :: h3 :: tl = all
    //    println(s"$h0, $h1, $h2, $h3")
    //    println(tl.size)
        getDir("server/gate/", (v) => {
          v.getFields("value").head match {
            case JsString(infoJson) => GateServerInfo.read(JsonParser(infoJson))
            case _ => throw new IllegalArgumentException(s"json error: $v")
          }
        }).foreach{println}
//    var list = GateServerInfo("s1", 1, "192.168.0.10", 9001, 32, 100) :: Nil
//    list = GateServerInfo("s2", 2, "192.168.0.10", 9002, 22, 100) :: list
//    list = GateServerInfo("s3", 3, "192.168.0.10", 9003, 19, 100) :: list
//    list.foreach { info =>
//      val json = GateServerInfo.write(info).asJsObject.compactPrint
//      println(json)
//      etcdWrite(s"server/gate/${info.id}", json)
//    }
  }

  def etcdWrite(key: String, value: String): Boolean = {
    val client = HttpClients.createSystem()
    val put = new HttpPut(getURI(key))
    val pairs = new util.ArrayList[NameValuePair](1)
    pairs.add(new BasicNameValuePair("value", value))
    val entity = new UrlEncodedFormEntity(pairs)
    put.setEntity(entity)
    val resp = client.execute(put)
    println(resp.getStatusLine.getStatusCode)
    println(resp.getEntity.toString)
    resp.getStatusLine.getStatusCode == 201
  }

  def getURI(key: String): URI = new URL(s"http://127.0.0.1:2379/v2/keys/$key").toURI

  def getDir[T](key: String, valueHandler: (JsObject) => T): List[T] = {
    val client = HttpClients.createSystem()
    try {
      val uri = new URL(s"http://127.0.0.1:2379/v2/keys/$key").toURI
      val get = new HttpGet(uri)
      val resp = client.execute(get)
      if (resp.getStatusLine.getStatusCode == 200) {
        val os = resp.getEntity.getContent
        try {
          val bin = new Array[Byte](resp.getEntity.getContentLength.toInt)
          os.read(bin)
          val str = new String(bin, Util.charset)
          val nodesArr = JsonParser(str).asJsObject.getFields("node").head.asJsObject.getFields("nodes")
          val nodes = nodesArr.head.asInstanceOf[JsArray]
          nodes.elements.map { node => valueHandler(node.asJsObject) }.toList
        } finally {
          if (os != null) os.close()
        }
      } else {
        println("error resp code: ", resp.getStatusLine.getStatusCode)
        List.empty
      }
    } catch {
      case e: Throwable =>
        e.printStackTrace()
        List.empty
    } finally {
      if (client != null) client.close()
    }
  }

}


