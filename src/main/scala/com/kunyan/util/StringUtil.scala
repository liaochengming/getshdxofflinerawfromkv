package com.kunyan.util

import java.io.{IOException, ByteArrayInputStream, ByteArrayOutputStream}
import java.util.zip.{ZipException, GZIPInputStream}

import sun.misc.BASE64Decoder

/**
 * Created by lcm on 2017/3/2.
 * 字符串的处理类
 */
object StringUtil {
  /**
   *
   * <p>Description:使用gzip进行解压缩</p>
   * @param compressedStr 要被解压的字符串
   * @return 解压后的字符串
   */
  def gunZip(compressedStr: String): String = {

    if (compressedStr == null) {
      return null
    }

    val out: ByteArrayOutputStream = new ByteArrayOutputStream
    var in: ByteArrayInputStream = null
    var ginZip: GZIPInputStream = null
    var compressed: Array[Byte] = null
    var decompressed: String = null

//    if(compressedStr.contains("<")) return ""

    try {

      compressed = new BASE64Decoder().decodeBuffer(compressedStr)
      in = new ByteArrayInputStream(compressed)
      ginZip = new GZIPInputStream(in)

      val buffer: Array[Byte] = new Array[Byte](1024)
      var offset: Int = -1

      while ( {
        offset = ginZip.read(buffer)
        offset
      } != -1) {

        out.write(buffer, 0, offset)
      }

      decompressed = out.toString
    }
    catch {
      case zip:ZipException =>
        println(compressedStr)
      case e: IOException =>
        e.printStackTrace()

    } finally {

      if (ginZip != null) {

        try {
          ginZip.close()
        }

        catch {

          case e: IOException =>
            e.printStackTrace()
        }

      }
      if (in != null) {

        try {
          in.close()
        }

        catch {

          case e: IOException =>
            e.printStackTrace()

        }
      }
      if (out != null) {

        try {
          out.close()
        }

        catch {

          case e: IOException =>
            e.printStackTrace()

        }
      }
    }

    decompressed
  }
}
