package com.bheap.silk.utils

import scala.util.matching.Regex

import java.io.File

object SilkScout {
  def getFilesInDirectoryOfType(directory: String, fileType: String) = {
    val userDir = new File(System.getProperty("user.dir"))
    (new File(userDir, directory)).listFiles.
      filter(_.isFile).filter(_.getName.endsWith("." + fileType))
  }

  def getRecursiveFilesInDirectoryOfType(file: File, regex: Regex): List[File] = {
	  val these = file.listFiles.toList
	  val good = these.filter(item => regex.findFirstIn(item.getName).isDefined)
	  good ++ these.filter(_.isDirectory).flatMap(getRecursiveFilesInDirectoryOfType(_,regex)).toList
	}
}
