package com.bheap.synapse.application

import scala.io.Source

import java.io.File

import com.codecommit.antixml._

import unfiltered.request._
import unfiltered.response._

import com.bheap.synapse.view.XmlView

class Application {

  val pageFiles = (new File("/Users/rossputin/.synapse/pages")).listFiles.
    filter(_.isFile).filter(_.getName.endsWith(".xml"))

  val ls: List[(String, String, String)] = pageFiles.toList.map {
    pageFile =>
      val pageXml = XML fromSource (Source fromURL ("file:%s" format pageFile))
      val url = (pageXml \ 'url \ text).head
      println(url.getClass.getName)
      val description = (pageXml \ 'description \ text).head
      println(description.getClass.getName)
      val template = (pageXml \ 'template \ text).head
      println(template.getClass.getName)
      (url, description, template)
  }

  val filters = ls.map {
    details =>
      unfiltered.filter.Planify {
        case GET(Path(details._1)) =>
          val view = new XmlView(details._3).view
          ResponseString(view.toString)
      }
  }
}

object Server {
  def main(args: Array[String]) {
    println("starting synapse on localhost, port %s" format 8080)
    val jetty = unfiltered.jetty.Http(8080)
    val app = new Application()
    app.filters.foreach(item => jetty.filter(item))
    jetty.run
  }
}
