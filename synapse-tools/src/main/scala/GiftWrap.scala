package com.bheap.synapse.tools

import scala.xml._

import java.io.{File, FileInputStream, FileOutputStream, FileWriter, IOException}

import org.fusesource.scalate.scuery.Transformer

import com.bheap.synapse.utils.SynapseScout

class GiftWrap(template: String, viewType: String) {
  val templateXml = XML.loadFile(System.getProperty("user.dir") + "/template/" + template)

  def build {
    wrap(inject)
    bundle(new File(System.getProperty("user.dir") + "/resource"), new File(System.getProperty("user.dir") + "/site/resource"))
  }

  // inject components into views, maps a list of files into a list of nodes
  def inject = {
    SynapseScout.getFilesInDirectoryOfType("view", "html").toList.map {
      item =>
        val viewXML = XML.loadFile(item)
        object transformer extends Transformer {
          val viewDiv = (viewXML \\ "div").filter(item => (item \ "@id").toString.contains("scp-"))
          viewDiv.foreach {
	          comp => 
              val compStruct = (comp \ "@id")(0).toString
              val cPathBits = compStruct.split("-").last.split("_")
              val cPath = cPathBits.head
              val cName = cPathBits.last
              val compXML = XML.loadFile(System.getProperty("user.dir") + "/component/" + cPath + "/" + cName + ".html")
              val compDiv = (compXML \\ "div").find(item => (comp \ "@id").text == compStruct) 
              $("div#" + compStruct).contents = compDiv.get
          }
        }
        val trans = transformer(viewXML)
        (item, trans(0))
    }
  }
  
  // wrap views in templates 
  def wrap(views: List[Tuple2[File, Node]]) {
    views.foreach {
      view =>
        object transformer extends Transformer {
          val contentDiv = (view._2 \\ "div").find(item => (item \ "@id").text == "synapse-content")
          $("div#synapse-template").contents = contentDiv.get
        }
        val trans = transformer(templateXml)
        
        val xhtml = postProcess(trans(0))
        
        // @todo use platform independent separator
        val fileName = view._1.toString.split("/").last
        val out = new FileWriter(System.getProperty("user.dir") + "/site/" + fileName)
        out.write(xhtml)
        out.flush
        out.close
    }
  }

  // bundle all resource files into the site output directory
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

  def postProcess(node: Node) = {
    Xhtml.toXhtml(node)
      .replace("&lt;", "<")
      .replace("&gt;", ">")
      .replace("&apos;", "'")
      .replace("&quot;", "\"")
      .replace("/&amp;", "&")
  }
}
