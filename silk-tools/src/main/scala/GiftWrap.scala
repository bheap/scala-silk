package com.bheap.silk.tools

import scala.xml._

import java.io.{File, FileWriter}

import org.fusesource.scalate.scuery.Transformer

import com.bheap.io.PathUtils

import com.bheap.silk.utils.SilkScout
import com.bheap.silk.utils.SilkBundle._

class GiftWrap(template: String, viewType: String) {
  val templateXml = XML.loadFile(System.getProperty("user.dir") + "/template/" + template)

  def build {
    wrap(inject)
    bundle(new File(System.getProperty("user.dir") + "/resource"), new File(System.getProperty("user.dir") + "/site/resource"))
  }

  // inject components into views, maps a list of files into a list of nodes
  def inject = {
    SilkScout.getRecursiveFilesInDirectoryOfType(new File(System.getProperty("user.dir") + "/view"), """.*\.html$""".r).map {
      item =>
        val viewXML = XML.loadFile(item)
        object transformer extends Transformer {
          val viewDiv = (viewXML \\ "div").filter(item => (item \ "@id").toString.contains("silk-component"))
          viewDiv.foreach {
	          comp => 
              val compStruct = (comp \ "@id")(0).toString
              // @todo use platform independent separator
              val cPathBits = compStruct.split(":").last.split("/")
              val cPath = cPathBits.head
              val cName = cPathBits.last
              // @todo use path independent separator
              val compXML = XML.loadFile(System.getProperty("user.dir") + "/component/" + cPath + "/" + cName + ".html")
              val compDiv = (compXML \\ "div").find(item => (comp \ "@id").text == compStruct) 
              $("div#" + compStruct.replaceAll(":", "").replaceAll("/", "")).contents = compDiv.get
          }
        }
        val parsedView = diluteSilkComponents(item)
        val parsedXML = XML.loadString(parsedView.get)
        val trans = transformer(parsedXML)
        (item, trans(0))
    }
  }
  
  // wrap views in templates 
  def wrap(views: List[Tuple2[File, Node]]) {
    views.foreach {
      view =>
        val templateTransformer = new TemplateTransformer(view._2)
        val templateResult = templateTransformer(templateXml)

        val anchorUAT = new URIAttributeTransformer("a", "href", view._1)
		    val anchorResult = anchorUAT(templateResult)

        val linkUAT = new URIAttributeTransformer("link", "href", view._1)
        val linkResult = linkUAT(anchorResult)

        val scriptUAT = new URIAttributeTransformer("script", "src", view._1)
        val scriptResult = scriptUAT(linkResult)

        val imageUAT = new URIAttributeTransformer("img", "src", view._1)
        val imageResult = imageUAT(scriptResult)

        val xhtml = postProcess(imageResult(0))
        
        val suffixPath = view._1.toString.split(System.getProperty("user.dir")).last.replace("/view/", "/site/")
        // @todo use platform independent separator
        val filePath = suffixPath.split("/").last
        val chkDir = System.getProperty("user.dir") + suffixPath.split(filePath).head
        if(!(new File(chkDir)).exists()) new File(chkDir).mkdirs

        val out = new FileWriter(System.getProperty("user.dir") + suffixPath)
        out.write(xhtml)
        out.flush
        out.close
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

  // load file into string, parse Silk component attribute values ready for css transformation
  def diluteSilkComponents(item: File) = {
    val source = scala.io.Source.fromFile(item)
    val content = source.mkString
    source.close
    val r = """(\"silk-component:[/a-z-A-Z_0-9:]+\")""".r
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

class TemplateTransformer(viewNode: Node) extends Transformer {
  val contentDiv = (viewNode \\ "div").find(item => (item \ "@id").text == "silk-view")
  $("div#silk-template").contents = contentDiv.get
}

class URIAttributeTransformer(element: String, attribute: String, view: File) extends Transformer {
  $(element).selectiveAttribute(attribute) {
    n =>
      val currentHref = (n \ ("@" + attribute)).toString
      // perform my criteria checks here, and regex to determine depth of location in path
      if (currentHref.contains("http:") || currentHref.contains("mailto:") || currentHref.contains("https:") || currentHref.contains("feed:") || currentHref.startsWith("#")) currentHref
      else {
        val rootPath = System.getProperty("user.dir") + "/view/"
        val viewDepth = (view.toString.split(rootPath).last count (item => item == '/')) + 1
        val urlDepth = currentHref count (item => item == '/')
        if (viewDepth > 1) {
          val urlSubPath = if (urlDepth == 0) "" else {
            (new File(rootPath + currentHref)).getParentFile.toString.split(rootPath).last
          }
          val urlPath = new File(rootPath + urlSubPath)
          val pathDiff = PathUtils.relativize(view.getParentFile, urlPath)
          pathDiff + "/" + (new File(rootPath + currentHref)).getName
        } else {
          currentHref
        }
      }
  }
}
