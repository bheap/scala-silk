package com.bheap.synapse.utils

import scala.util.matching.Regex

import java.io.File

object SynapseScout {
  def getFilesInDirectoryOfType(directory: String, fileType: String) = {
    // @todo use path independent separator
    (new File(System.getProperty("user.dir") + "/" + directory)).listFiles.
      filter(_.isFile).filter(_.getName.endsWith("." + fileType))
  }

  def getRecursiveFilesInDirectoryOfType(file: File, regex: Regex): List[File] = {
	  val these = file.listFiles.toList
	  val good = these.filter(item => regex.findFirstIn(item.getName).isDefined)
	  good ++ these.filter(_.isDirectory).flatMap(getRecursiveFilesInDirectoryOfType(_,regex)).toList
	}
}
