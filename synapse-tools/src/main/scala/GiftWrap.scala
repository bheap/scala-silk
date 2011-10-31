package com.bheap.synapse.tools

import scala.xml._

import java.io.File

import org.fusesource.scalate.scuery.Transformer

class GiftWrap(template: String, viewType: String) {
  val templateXml = XML.loadFile(System.getProperty("user.home") + "/.synapse/sites/" + System.getProperty("ss") + "/templates/" + template)

  def getViewFiles = {
    (new File(System.getProperty("user.home") + "/.synapse/sites/" + System.getProperty("ss") + "/views")).listFiles.
      filter(_.isFile).filter(_.getName.endsWith("." + viewType))
  }

  def wrap {
    getViewFiles.toList.foreach {
      item =>
        val view = XML.loadFile(item)
        object transformer extends Transformer {
          val contentDiv = (view \\ "div").find(item => (item \\ "@id").text == "synapse-content")
          $("div#synapse-template").contents = contentDiv.get
        }
        val trans = transformer(templateXml)
        XML.save(item.toString, trans(0))
    }
  }
}
