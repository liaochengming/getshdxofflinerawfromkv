package com.kunyan.util

import java.io.{File, FileInputStream, FileOutputStream, IOException}
import java.nio.ByteBuffer
import java.nio.channels.FileChannel

/**
 * Created by lcm on 2017/3/6.
 * 合并文件
 */
object MergedFileUtil {


  def merged(files_path: String, outfileName: String) = {

    val file: File = new File(files_path)
    val array: Array[File] = file.listFiles

    if ((null != array) && (array.length != 0)) {

      val fileNames: Array[String] = new Array[String](array.length)

      var i: Int = 0
      while (i < array.length) {
        fileNames(i) = files_path + "/"+ array(i).getName
        i += 1
      }

      mergeFiles(outfileName, fileNames)
      delete(fileNames,files_path)
    }
  }

  def mergeFiles(outFile: String, files: Array[String]) = {

    var outChannel: FileChannel = null
    System.out.println("Merge " + files + " into " + outFile)

    try {

      outChannel = new FileOutputStream(outFile).getChannel

      for (f <- files) {

        val fc: FileChannel = new FileInputStream(f).getChannel
        val bb: ByteBuffer = ByteBuffer.allocate(8192)

        while (fc.read(bb) != -1) {
          bb.flip
          outChannel.write(bb)
          bb.clear
        }

        fc.close()
      }
      System.out.println("Merged!! ")
    }
    catch {

      case ioe: IOException =>
        ioe.printStackTrace()

    } finally {

      try {
        if (outChannel != null) outChannel.close()
      }

      catch {

        case iOException: IOException =>
          iOException.printStackTrace()
      }
    }
  }

  def delete(fileNames:Array[String],files_path:String)={

    for (fileName <- fileNames) {

      val f: File = new File(fileName)
      if (f.exists) f.delete

    }

    val dir: File = new File(files_path)
    if (dir.exists) dir.delete
  }
}
