package com.bheap.silk.utils

import scala.util.matching.Regex

import java.io.File

object SilkScout {

  def getRecursiveFilesInDirectoryOfType(file: File, regex: Regex): List[File] = {
	  val these = file.listFiles.toList
	  val good = these.filter(item => regex.findFirstIn(item.getName).isDefined)
	  good ++ these.filter(_.isDirectory).flatMap(getRecursiveFilesInDirectoryOfType(_,regex)).toList
	}
}
