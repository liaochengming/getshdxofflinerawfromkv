package com.kunyan.util

import java.text.SimpleDateFormat
import java.util.Date

/**
 * Created by lcm on 2017/3/2.
 * 规范时间的工具类
 */
object TimeUtil {

  /**
   * 获取当前时间hour小时之前的时间字符串
   *
   * @param hour 固定小时，指定为多少小时之前
   * @return yyyyMMddHH格式的字符串
   */
  def getYearToHour(hour: Int): String = {

    val oldTime: Date = new Date(System.currentTimeMillis - hour * 60 * 60 * 1000)
    val sdFormatter: SimpleDateFormat = new SimpleDateFormat("yyyyMMddHH")
    sdFormatter.format(oldTime)

  }

  /**
   * 格式化分钟和秒钟
   * @param time Int类型的分钟和秒钟
   * @return String类型的分钟和秒钟
   */
  def formatMinAndSec(time:Int):String = {

    if (time < 10) "0" + time else "" + time

  }

  /**
   * 获取时间戳的小时时间
   * @param time 时间戳
   * @return 小时时间
   */
  def getHour(time:Long):Int={
    val date: Date = new Date(time)
    val sdFormatter: SimpleDateFormat = new SimpleDateFormat("HH")
    sdFormatter.format(date).toInt
  }

}
