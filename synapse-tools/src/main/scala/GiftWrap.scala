package com.bheap.synapse.tools

import scala.xml._

import java.io.File

import org.fusesource.scalate.scuery.Transformer

class GiftWrap(template: String, viewDirectory: String, viewType: String) {
  val templateXml = XML.loadFile("/Users/rossputin/.synapse/sites/bheap-example/templates/" + template)

  def getViewFiles = {
    (new File("/Users/rossputin/.synapse/sites/bheap-example/views")).listFiles.
      filter(_.isFile).filter(_.getName.endsWith("." + viewType))
  }

  def wrap {
    getViewFiles.toList.foreach {
      item =>
        val view = XML.loadFile(item)
        object transformer extends Transformer {
          $("div#synapse-template").contents = view \\ "div"
        }
        val trans = transformer(templateXml)
        XML.save(item.toString, trans(0))
    }
  }
}


