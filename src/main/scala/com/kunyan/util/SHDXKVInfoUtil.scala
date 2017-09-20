package com.kunyan.util

import java.io.{BufferedReader, InputStream, InputStreamReader}
import java.net._
import java.security.MessageDigest
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

import com.kunyan.control.Parameter
import org.apache.commons.codec.binary.Hex
import org.json.{JSONException, JSONObject}
import sun.misc.BASE64Decoder

/**
 * Created by lcm on 2017/3/2.
 * 从上海电信KV获取数据的工具类
 */
object SHDXKVInfoUtil {

  /**
   * 获取电信的token的方法
   * @return 获取的token字符串
   */
  def getToken: String = {

    val apiKey: String = Parameter.API_KEY
    val userName: String = Parameter.KUN_YAN_USER_NAME
    val password: String = Parameter.KUN_YAN_PASSWORD

    val getToken: String = "getToken?apiKey=" + apiKey + "&" + "sign=" + sign(md5Encode(password), userName + apiKey)

    try {

      var token = doGet(getToken)

      while (token == "time out") {
        token = doGet(getToken)
      }
      println(token)
      val jbToken: JSONObject = new JSONObject(token)

      jbToken.getString("result")
    } catch {
      case exception: Exception =>
        ""
    }

  }

  /**
   * 对字符串做md5编码
   *
   * @param str 需要md5编码的字符串
   * @return 做了md5编码之后的字符串
   */
  private def md5Encode(str: String): String = {

    var md5: MessageDigest = null

    try {

      md5 = MessageDigest.getInstance("MD5")

    } catch {

      case ioException: Exception =>
        ioException.printStackTrace()
        return ""

    }

    val byteArray: Array[Byte] = str.getBytes("UTF-8")
    val md5Bytes: Array[Byte] = md5.digest(byteArray)
    val hexValue: StringBuilder = new StringBuilder

    for (md5Byte <- md5Bytes) {

      val `val`: Int = md5Byte.toInt & 0xff

      if (`val` < 16) {
        hexValue.append("0")
      }

      hexValue.append(Integer.toHexString(`val`))
    }

    hexValue.toString()
  }

  /**
   * 获取http请求时所需的签名
   *
   * @param secretKey 经过md5加密处理的秘钥
   * @param data      用户名称和apiKey
   * @return 签名字符串
   */
  def sign(secretKey: String, data: String): String = {

    val signingKey: SecretKeySpec = new SecretKeySpec(secretKey.getBytes, Parameter.HMAC_SHA1_ALGORITHM)
    val mac: Mac = Mac.getInstance(Parameter.HMAC_SHA1_ALGORITHM)
    mac.init(signingKey)
    val rawHmac: Array[Byte] = mac.doFinal(data.getBytes)

    Hex.encodeHexString(rawHmac)

  }


  /**
   * 此方法为http请求
   *
   * @param url 指定所需的请求
   * @return 请求到的数据
   */
  def doGet(url: String): String = {

    val localURL: URL = new URL(Parameter.SH_KV_URL_HEAD + url)
//    println(Parameter.SH_KV_URL_HEAD + url)
    val connection: URLConnection = localURL.openConnection
    val httpURLConnection: HttpURLConnection = connection.asInstanceOf[HttpURLConnection]

    httpURLConnection.setConnectTimeout(3000)
    httpURLConnection.setReadTimeout(3000)
    httpURLConnection.setRequestProperty("Connection", "keep-alive")
    httpURLConnection.setRequestProperty("Accept-Charset", "utf-8")
    httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")

    var inputStream: InputStream = null
    var inputStreamReader: InputStreamReader = null
    var reader: BufferedReader = null
    val resultBuilder: StringBuilder = new StringBuilder
    var tempLine: String = null
    var code = 0

    try {
      code = httpURLConnection.getResponseCode
      if (code == 200) {

        inputStream = httpURLConnection.getInputStream
        inputStreamReader = new InputStreamReader(inputStream)
        reader = new BufferedReader(inputStreamReader)

        while ( {
          tempLine = reader.readLine
          tempLine
        } != null) {

          resultBuilder.append(tempLine)

        }
      }

    } catch {

      case timeOut: SocketTimeoutException =>
        resultBuilder.append("time out")

      case exception: Exception =>
//        println("exception 相应码：" +         code)
        exception.printStackTrace()

    } finally {

      if (reader != null) {
        reader.close()
      }

      if (inputStreamReader != null) {
        inputStreamReader.close()
      }

      if (inputStream != null) {
        inputStream.close()
      }
      if(httpURLConnection != null){
        httpURLConnection.disconnect()
      }

    }

    resultBuilder.toString()
  }


  /**
   * 从KV表取一条数据
   * @param key 从KV取数据的key
   * @param token 发送http请求所需的参数
   * @return 取到的数据字符串
   */
  def getAData(key: String, token: String): String = {

    val url = "kv/getValueByKey?token=" + token + "&table=" + Parameter.KV_TABLE_NAME + "&key=" + key

    var aData = ""

    var back = doGet(url)

    while (back == "time out") {
      back = doGet(url)
    }

    if (back != "") {

      try {

        val jsonOBJ = new JSONObject(back)
        val result = jsonOBJ.getString("result")

        if (result != "null") {
          val value = new JSONObject(result).getString("value")
          val raw = new String(new BASE64Decoder().decodeBuffer(value))
          aData = StringUtil.gunZip(raw)
        }

      } catch {

        case jSONException: JSONException =>
          jSONException.printStackTrace()

      }
    }

    aData
  }

}
