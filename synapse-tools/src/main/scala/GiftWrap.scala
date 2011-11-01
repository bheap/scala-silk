package com.bheap.synapse.tools

import scala.xml._

import java.io.File

import org.fusesource.scalate.scuery.Transformer

class GiftWrap(template: String, viewType: String) {
  val templateXml = XML.loadFile(System.getProperty("user.dir") + "/template/" + template)

  def getViewFiles = {
    (new File(System.getProperty("user.dir") + "/content")).listFiles.
      filter(_.isFile).filter(_.getName.endsWith("." + viewType))
  }

  def wrap {
    getViewFiles.toList.foreach {
      item =>
        val view = XML.loadFile(item)
        object transformer extends Transformer {
          val contentDiv = (view \\ "div").find(item => (item \ "@id").text == "synapse-content")
          $("div#synapse-template").contents = contentDiv.get
        }
        val trans = transformer(templateXml)
        // @todo use platform independent separator
        val fileName = item.toString.split("/").last
        XML.save(System.getProperty("user.dir") + "/target/" + fileName, trans(0))
    }
  }
}
