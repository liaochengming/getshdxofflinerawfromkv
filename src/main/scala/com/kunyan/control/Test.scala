package com.kunyan.control

import java.text.SimpleDateFormat
import java.util.Date

import scala.collection.mutable.ListBuffer

/**
 * Created by lcm on 2017/3/3.
 *
 */
object Test extends App{

//  val data = new ListBuffer[String]
//  data.+=("a")
//  data.+=("b")
//  println(data)
//  val data1 = new ListBuffer[String]
//  data1.+=("c")
//  data1.+=("d")
//  val d = data.toList ::: data1.toList
//  println(d)
//
//  val oldTime: Date = new Date(1488506460281L)
//  val sdFormatter: SimpleDateFormat = new SimpleDateFormat("HH")
//  println(sdFormatter.format(oldTime))
  val listBuffer = new ListBuffer[Int]

  for(x <- 0 to 10000000){
    listBuffer.+=(x)
  }

  val time1 = System.currentTimeMillis()

  listBuffer.toList.foreach(x =>{
    if(x % 10000 == 1){
      println(x)
    }
  })

  val time2 = System.currentTimeMillis()

  for(x <-listBuffer){
    if(x % 10000 == 1){
      println(x)
    }
  }

  val time3 = System.currentTimeMillis()

  println("foreach =>" + (time2 -time1))

  println("for =>" + (time3 -time2))
}
