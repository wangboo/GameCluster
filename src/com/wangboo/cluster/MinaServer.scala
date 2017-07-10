package com.wangboo.cluster

import java.net.InetSocketAddress

import com.google.protobuf.GeneratedMessage
import org.apache.mina.core.buffer.IoBuffer
import org.apache.mina.core.service.IoHandler
import org.apache.mina.core.session.IoSession
import org.apache.mina.filter.codec._
import org.apache.mina.transport.socket.nio.NioSocketAcceptor

/**
  * protobuf协议解包
  * 包头3位|包体
  * Created by wangboo on 2017/6/15.
  */
class ProtobufDecoder[Pb <: GeneratedMessage](pbDecoder: (Array[Byte]) => Pb) extends CumulativeProtocolDecoder {
  var readLen = 0
  var state = 0
  // 0 readLen, 1 readBody
  val lenBytes = new Array[Byte](3)

  override def doDecode(session: IoSession, in: IoBuffer, out: ProtocolDecoderOutput): Boolean = {
    if (state == 0) {
      if (in.remaining() > 2) {
        in.get(lenBytes, 0, 3)
        readLen = (lenBytes(0) << 16) + (lenBytes(1) << 8) + lenBytes(0)
        state = 1
        readBody(in, out)
      } else {
        false
      }
    } else {
      readBody(in, out)
    }
  }

  def readBody(in: IoBuffer, out: ProtocolDecoderOutput): Boolean = {
    if (in.remaining() >= readLen) {
      val bin = new Array[Byte](readLen)
      in.get(bin, 0, readLen)
      val msg = pbDecoder.apply(bin)
      out.write(msg)
    }
    in.remaining() > 0
  }
}

class MinaServer[Pb <: GeneratedMessage](ioHandler: IoHandler, pbDecoder: (Array[Byte]) => Pb, port: Int, processCount: Int = 4) {

  val acceptor: NioSocketAcceptor = new NioSocketAcceptor(processCount)
  val pbEncoder = new ProtocolEncoder {
    override def encode(session: IoSession, message: scala.Any, out: ProtocolEncoderOutput): Unit = {
      message match {
        case pb: GeneratedMessage =>
          val bin = pb.toByteArray
          val len = bin.length
          val pkgSize = len + 3
          val buff = IoBuffer.allocate(pkgSize)
          buff.put(0, ((len >> 16) & 0xff).toByte)
          buff.put(1, ((len >> 8) & 0xff).toByte)
          buff.put(2, (len & 0xff).toByte)
          buff.put(bin, 3, pkgSize)
          buff.flip()
          out.write(buff)
          out.flush()
      }
    }

    override def dispose(session: IoSession): Unit = None
  }
  val codecFactory = new ProtocolCodecFactory() {
    override def getDecoder(session: IoSession): ProtocolDecoder = new ProtobufDecoder(pbDecoder)

    override def getEncoder(session: IoSession): ProtocolEncoder = pbEncoder
  }

  def start(): Unit = {
    acceptor.getFilterChain.addLast("codec", new ProtocolCodecFilter(codecFactory))
    acceptor.setHandler(ioHandler)
    acceptor.getSessionConfig.setReadBufferSize(2048)
    acceptor.getSessionConfig.setWriterIdleTime(40)
    acceptor.bind(new InetSocketAddress(port))
  }

  def shutdown(): Unit = {
    acceptor.dispose()
  }

}
