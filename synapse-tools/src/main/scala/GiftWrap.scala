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
    SynapseScout.getRecursiveFilesInDirectoryOfType(new File(System.getProperty("user.dir") + "/view"), """.*\.html$""".r).map {
      item =>
        val viewXML = XML.loadFile(item)
        object transformer extends Transformer {
          val viewDiv = (viewXML \\ "div").filter(item => (item \ "@id").toString.contains("synapse-component"))
          viewDiv.foreach {
	          comp => 
              val compStruct = (comp \ "@id")(0).toString
              val cPathBits = compStruct.split(":").last.split("/")
              val cPath = cPathBits.head
              val cName = cPathBits.last
              val compXML = XML.loadFile(System.getProperty("user.dir") + "/component/" + cPath + "/" + cName + ".html")
              val compDiv = (compXML \\ "div").find(item => (comp \ "@id").text == compStruct) 
              $("div#" + compStruct.replaceAll(":", "").replaceAll("/", "")).contents = compDiv.get
          }
        }
        val parsedView = diluteSynapseComponents(item)
        val parsedXML = XML.loadString(parsedView.get)
        val trans = transformer(parsedXML)
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
        
        val suffixPath = view._1.toString.split(System.getProperty("user.dir")).last.replace("/view/", "/site/")
        // @todo use platform independent separator
        val filePath = suffixPath.split("/").last
        val chkDir = System.getProperty("user.dir") + suffixPath.split(filePath).head
        if(!(new File(chkDir)).exists()) new File(chkDir).mkdir
        
        val out = new FileWriter(System.getProperty("user.dir") + suffixPath)
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
      .replace("<html>", "<!doctype html><!--[if lt IE 7 ]><html class=\"no-js ie6\" lang=\"en\"><![endif]--><!--[if IE 7 ]><html class=\"no-js ie7\" lang=\"en\"><![endif]--><!--[if IE 8 ]><html class=\"no-js ie8\" lang=\"en\"><![endif]--><!--[if (gte IE 9)|!(IE)]><!--><html class=\"no-js\" lang=\"en\"><!--<![endif]-->")
  }

  // load file into string, parse synapse component attribute values ready for css transformation
  def diluteSynapseComponents(item: File) = {
    val source = scala.io.Source.fromFile(item)
    val content = source.mkString
    source.close
    val r = """(\"synapse-component:[/a-z-A-Z_0-9:]+\")""".r
    val matchMap = r.findAllIn(content).toList.map(item => Map(item -> item.replaceAll(":", "").replaceAll("/", "")))
    // @todo iterate over the items in the map applying each replace based on them within the content string
    val firstP = if (matchMap.size > 0) Some(content.replace(matchMap.head.keySet.head, matchMap.head.values.head)) else None
    val secondP = if (matchMap.size > 1) Some(firstP.get.replace(matchMap.last.keySet.head, matchMap.last.values.head)) else None
    val parsedStr = 
      if (secondP.isDefined) secondP 
      else if (firstP.isDefined) firstP
      else Some(content)
    parsedStr
  }
}
