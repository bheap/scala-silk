package com.bheap.synapse.tools

import scala.xml._

import java.io.{File, FileInputStream, FileOutputStream, FileWriter, IOException}

import org.fusesource.scalate.scuery.Transformer

class GiftWrap(template: String, viewType: String) {
  val templateXml = XML.loadFile(System.getProperty("user.dir") + "/template/" + template)

  def getViewFiles = {
    (new File(System.getProperty("user.dir") + "/content")).listFiles.
      filter(_.isFile).filter(_.getName.endsWith("." + viewType))
  }

  def build {
    wrap
    bundle(new File(System.getProperty("user.dir") + "/resource"), new File(System.getProperty("user.dir") + "/site/resource"))
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
          .replace("&lt;", "<")
          .replace("&gt;", ">")
          .replace("&apos;", "'")
          .replace("&quot;", "\"")
          .replace("/&amp;", "&");
        
        // @todo use platform independent separator
        val fileName = item.toString.split("/").last
        val out = new FileWriter(System.getProperty("user.dir") + "/site/" + fileName)
        out.write(xhtml)
        out.flush
        out.close
    }
  }

  def bundle(src: File, dst: File) {
    if (src.isDirectory) {
      if(!dst.exists()) dst.mkdir

      val files = src.list
      files foreach {
        file =>
          val srcFile = new File(src, file)
          val dstFile = new File(dst, file)
          bundle(srcFile, dstFile)
      }
    } else {
      dst.createNewFile
      dst.getCanonicalFile.getParentFile.mkdirs
      
			new FileOutputStream(dst) getChannel() transferFrom(
			    new FileInputStream(src) getChannel, 0, Long.MaxValue )
    }
  }
}
