package com.kunyan.control

import java.io.{File, PrintWriter}
import java.text.SimpleDateFormat
import java.util.Date
import java.util.concurrent.{TimeUnit, Executors}

import com.kunyan.util.{MergedFileUtil, SHDXKVInfoUtil, TimeUtil}
import kunyan.com.MySendMail

import scala.collection.mutable.ListBuffer
import scala.util.control.Breaks._

/**
 * Created by lcm on 2017/3/2.
 * 从上海电信KV获取离线原始数据的程序
 */
object GetRaw {


  def main(args: Array[String]) {

    val token = SHDXKVInfoUtil.getToken
    val currentTime = TimeUtil.getYearToHour(args(2).toInt)
    val currentDate = currentTime.substring(0, 8)
    val currentHour = currentTime.substring(8)
    val currentDataDirStr = args(1) + currentDate + "/" + currentHour
    val pool = Executors.newFixedThreadPool(60)

    for (minInt <- 0 to args(0).toInt) {

      val minStr = TimeUtil.formatMinAndSec(minInt)
      val call = new MyRunnable(currentTime, minStr, token, currentDataDirStr)

      pool.submit(call)

    }

    pool.shutdown()

    var over = false
    while (!over) over = pool.awaitTermination(5000, TimeUnit.MILLISECONDS)

    val outFileName = currentDataDirStr + ".txt"
    MergedFileUtil.merged(currentDataDirStr, outFileName)

    val dataFile = new File(outFileName)
    val sdf = new SimpleDateFormat("yyyy年MM月dd日 E HH时 mm分 ss秒 S毫秒")

    if (dataFile.exists() && dataFile.isFile) {

      if (dataFile.length() < 500000) {

        val size = dataFile.length() / 1024
        MySendMail.sendMail("离线<原始数据>接收异常", "接收到< " + currentTime + " >的数据文件大小为："
          + size + " KB" + "<br>" + sdf.format(new Date()))
      }

    } else {

      MySendMail.sendMail("离线<原始数据>接收异常", "没有数据文件")
    }

  }

  /**
   * 获取一分钟数据
   * @param minStr 分钟
   * @param token 请求参数
   */
  def outOneMinData(currentTime:String, minStr: String, token: String, dirPath: String) = {

    for (secInt <- 0 to 59) {

      val data = new ListBuffer[String]
      val secStr = TimeUtil.formatMinAndSec(secInt)

      breakable {

        var index = 0
        var empty = 0

        while (index < 8000) {

          index = index + 1
          val key = currentTime + minStr + secStr + "_" + index
          val aData = SHDXKVInfoUtil.getAData(key, token)

          if (aData != "") {
            data.+=(aData)
            empty = 0
          } else {
            empty = empty + 1
          }
          if (empty > 10) {
            break()
          }
        }
      }

      //写出文件
      val dir = new File(dirPath)
      if (!dir.exists()) dir.mkdirs()
      val write = new PrintWriter(dirPath + "/" + minStr + secStr + ".txt")

      for (aData <- data) {
        write.write(aData + "\n")
      }

      write.close()
      data.clear()

    }
  }

}

