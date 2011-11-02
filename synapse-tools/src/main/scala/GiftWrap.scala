package com.bheap.synapse.tools

import scala.xml._

import java.io.{File, FileWriter}

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
        val xhtml = Xhtml.toXhtml(trans(0))
        
        // @todo use platform independent separator
        val fileName = item.toString.split("/").last
        val out = new FileWriter(System.getProperty("user.dir") + "/site/" + fileName)
        out.write(xhtml)
        out.flush
        out.close
    }
  }
}
