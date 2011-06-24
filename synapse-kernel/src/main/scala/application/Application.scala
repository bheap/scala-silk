package com.bheap.synapse.application

import scala.io.Source
import scala.collection.mutable.{Set => MSet}

import java.io.File

import unfiltered.filter.Planify
import unfiltered.jetty.Http
import unfiltered.request._
import unfiltered.response._

import com.bheap.synapse.utils.SynapseScout
import com.bheap.synapse.view.XmlView

/** Builds a web application out of a site configuration.
  *
  * A web application is generated out of a directory structure representing a site.
  *
  * @author <a href="mailto:ross@bheap.co.uk">rossputin</a>
  * @since 1.0 */
class Application {

  val jetty = Http(8080)

  /** Returns information on each view including name and path. */
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

  /** Returns a list of filters for a Jetty Http instance. */
  def getFilters = {
    prepareFilterDetails.map {
      details =>
        Planify {
          case GET(Path(details._1)) =>
            val view = new XmlView(details._2).view
            ResponseString(view.toString)
        }
    }
  }

  /** Set up a Jetty Http instance. */
  def initialise {
	val filters = getFilters
    println("filters are : " + filters)
    filters.foreach(item => jetty.filter(item))
    jetty.filter(
      Planify {
        case GET(Path("/reload")) =>
          reload
          ResponseString("<html><body><h1>Synapse has reloaded</h1></body></html>")
      }
    )
  }

  /** Start the Jetty Http instance. */
  def start {
    jetty.run
  }
}

/** Starts up a Jetty Http instance encapsulating our Synapse web application. */
object Server {
  def main(args: Array[String]) {
    println("Starting synapse on localhost, port %s" format 8080)
    val app = new Application()
    app.initialise
    app.start
  }
}
