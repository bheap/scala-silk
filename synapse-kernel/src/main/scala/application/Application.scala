package com.bheap.synapse.application

import scala.io.Source

import com.codecommit.antixml._

import unfiltered.request._
import unfiltered.response._

class Application {

  val pageFiles = (new java.io.File("/Users/rossputin/.synapse/pages")).listFiles.
    filter(_.isFile).filter(_.getName.endsWith(".xml"))

  val ls: List[(java.lang.String, java.lang.String)] = pageFiles.toList.map {
    pageFile =>
      val pageXml = XML fromSource (Source fromURL ("file:%s" format pageFile))
      val url = (pageXml \ 'url \ text).head
      println("url is : %s" format url)
      println(url.getClass.getName)
      val description = (pageXml \ 'description \ text).head
      println("description is : %s" format description)
      println(description.getClass.getName)
      (url, description)
  }

  println("ls is : " + ls)

  val filters = ls.map {
    details =>
      unfiltered.filter.Planify {
        case GET(Path(details._1)) => ResponseString("<html><body><h1>%s</h1></body></html>" format details._2)
      }
  }

  println("filters is : " + filters)
  println("size of filters is : " + filters.size)

  /*val src = XML fromSource (Source fromURL ("file:/Users/rossputin/.synapse/pages/index.xml"))

  val p1 = unfiltered.filter.Planify {
    case GET(Path("/")) => Html(<html><body><h1>root path</h1></body></html>)
  }
  val p2 = unfiltered.filter.Planify {
    case GET(Path("/deeper/path")) => Html(<html><body><h1>deeper path</h1></body></html>)
  }
  val p3 = unfiltered.filter.Planify {
    case GET(Path("/deepest/path")) => Html(<html><body><h1>deepest path</h1></body></html>)
  }
  val filters = List(p1, p2, p3)*/
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
