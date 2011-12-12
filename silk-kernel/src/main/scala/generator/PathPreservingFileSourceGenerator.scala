package com.bheap.silk.generator

import scala.util.matching.Regex
import scala.xml._

import java.io.File

import com.bheap.silk.utils.SilkScout

/** Defines some generators.
  *
  * Source different contents and convert them to something Silk can work with.
  *
  * @author <a href="mailto:ross@bheap.co.uk">rossputin</a>
  * @since 1.0 */
object PathPreservingFileSourceGenerator {

  def generateFromXHTML(source: File) = {
    generateXmlFromFileSource(source, """.*\.html$""".r)
  }

  def generateXmlFromFileSource(source: File, mimeType: Regex) = {
    SilkScout.getRecursiveFilesInDirectoryOfType(source, mimeType).map {
      item =>
        val viewXML = XML.loadFile(item)
        (item, viewXML)
    }
  }
}
