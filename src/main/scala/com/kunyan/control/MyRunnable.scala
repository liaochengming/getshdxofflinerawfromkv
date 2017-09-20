package com.kunyan.control

/**
 * Created by lcm on 2017/3/3.
 *
 */
class MyRunnable(currentTime:String, minStr: String, token: String, dirPath:String) extends Runnable{


  override def run(): Unit = {
  GetRaw.outOneMinData(currentTime,minStr,token,dirPath)
}

}
