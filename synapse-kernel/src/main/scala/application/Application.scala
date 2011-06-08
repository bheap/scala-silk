package com.bheap.synapse.application

import scala.io.Source
import scala.collection.mutable.{Set => MSet}

import java.io.File

import com.codecommit.antixml._

import unfiltered.request._
import unfiltered.response._

import com.bheap.synapse.utils.SynapseScout
import com.bheap.synapse.view.XmlView

class Application {

  val filterSet = MSet.empty[String]

  val jetty = unfiltered.jetty.Http(8080)

  def prepareFilterDetails = {
    SynapseScout.getFilesInDirectoryOfType("views", "html").toList.map {
      item =>
        val file = item.toString
        val viewXml = file.substring(file.lastIndexOf("/") + 1)
        val path = file.substring(file.lastIndexOf("/")).split(".html")(0) match {
          case "/index" => "/"
          case pth => pth
        }
        (path, viewXml)
    }
  }

  def getFilters(reload: Boolean) = {
    prepareFilterDetails.map {
      details =>
        unfiltered.filter.Planify {
          case GET(Path(details._1)) =>
            val view = new XmlView(details._2).view
            ResponseString(view.toString)
        }
    }
  }

  def initialise {
	val filters = getFilters(reload=false)
    println("filters are : " + filters)
    filters.foreach(item => jetty.filter(item))
  }

  def start {
    jetty.run
  }

  /*def getPageFiles = {
    (new File("/Users/rossputin/.synapse/pages")).listFiles.
      filter(_.isFile).filter(_.getName.endsWith(".xml"))
  }

  def getPageConfigs(reload: Boolean) = {
    val configs = getPageFiles.toList.map {
      pageFile =>
        val pageXml = XML fromSource (Source fromURL ("file:%s" format pageFile))
        val url = (pageXml \ 'url \ text).head
        val description = (pageXml \ 'description \ text).head
        val view = (pageXml \ 'view \ text).head
        (url, description, view)
    }
    val filteredConfigs = if (reload) configs.filter(item => !filterSet.contains(item._1)) else configs
    filteredConfigs.foreach(filterSet += _._1)
    filteredConfigs
  }

  def getFilters(reload: Boolean) = {
    getPageConfigs(reload).map {
      details =>
        unfiltered.filter.Planify {
          case GET(Path(details._1)) =>
            val view = new XmlView(details._3).view
            ResponseString(view.toString)
        }
    }
  }

  def initialise {
    getFilters(reload=false).foreach(item => jetty.filter(item))
    jetty.filter(
      unfiltered.filter.Planify {
        case GET(Path("/reload")) =>
          reload
          ResponseString("<html><body><h1>Synapse has reloaded</h1></body></html>")
      }
    )
  }

  def start {
    jetty.run
  }

  def reload {
    getFilters(reload=true).foreach(item => jetty.filter(item))
  }*/
}

object Server {
  def main(args: Array[String]) {
    println("Starting synapse on localhost, port %s" format 8080)
    val app = new Application()
    app.initialise
    app.start
  }
}
