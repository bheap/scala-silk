package com.bheap.silk.utils

import scala.util.matching.Regex

import java.io.File

/** Convenience recursive directory scan.
  *
  * Used to detect structure of site to spin.
  *
  * @author <a href="mailto:ross@bheap.co.uk">rossputin</a>
  * @since 1.0 */
object SilkScout {

  def getRecursiveFilesInDirectoryOfType(file: File, regex: Regex): List[File] = {
	  val these = file.listFiles.toList
	  val good = these.filter(item => regex.findFirstIn(item.getName).isDefined)
	  good ++ these.filter(_.isDirectory).flatMap(getRecursiveFilesInDirectoryOfType(_,regex)).toList
	}
}
