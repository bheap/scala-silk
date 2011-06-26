package com.bheap.synapse.application

import scala.io.Source
import scala.collection.mutable.{Map => MuMap}

import java.io.File

import unfiltered.filter.Planify
import unfiltered.jetty.Http
import unfiltered.request._
import unfiltered.response._

import com.bheap.synapse.actors._
import com.bheap.synapse.utils.SynapseScout


/** Builds a web application out of a site configuration.
  *
  * For synapse, a site configuration is a directory structure containing views.
  *
  * @author <a href="mailto:ross@bheap.co.uk">rossputin</a>
  * @since 1.0 */
class Application {

  val jetty = Http(8080)
  val viewActors = MuMap[String, ViewServerActor]()

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
  def getFilters(details: List[Tuple2[String, String]]) = {
    details.foreach {
      details =>
        val viewActor = getViewActor(details._2)
        viewActors += details._1 -> viewActor
    }
    details.map {
      details =>
        Planify {
          case GET(Path(details._1)) =>
            val viewActor = viewActors(details._1)
            val view = viewActor !? Render
			view match {
			  case s: String => ResponseString(s)
			  case _ => ResponseString(<html><body><h1>Sorry, there was an error</h1></body></html>.toString)
			}
        }
    }
  }

  /** Return a view actor with the correct view server wired in. */
  def getViewActor(viewPath: String) = {
    val viewActor = new ViewServerActor
    viewActor.start
    val vs = new GETViewServer(viewPath)
    viewActor ! HotSwap(vs)
    viewActor
  }

  /** Set up a Jetty Http instance.
    *
    * @todo work through mutable map of view serving actors and hotswap if necessary
    */
  def initialise {
    val preparedFilterDetails = prepareFilterDetails
    println("added views are : " + preparedFilterDetails)
	val filters = getFilters(prepareFilterDetails)
    filters.foreach(item => jetty.filter(item))
	jetty.filter(
      Planify {
        case GET(Path("/reload")) =>
          reload
          ResponseString(<html><body><h1>Synapse has reloaded</h1></body></html>.toString)
      }
    )
  }

  /** Start the Jetty Http instance. */
  def start {
    jetty.run
  }

  /** Rebuild the site from the site configuration. 
    *
    * First we add any new views that may have been added.
    * Then we convert all views that have been removed to 404's. */
  def reload {
    val filterDetails = prepareFilterDetails
    val uniqueFilterDetails = filterDetails.filter(item => !viewActors.keySet.contains(item._1))
    println("added views are : " + uniqueFilterDetails)
    val filters = getFilters(uniqueFilterDetails)
    filters.foreach(item => jetty.filter(item))

    val deletedViews = viewActors.keySet.diff(filterDetails.map(item => item._1).toSet)
    println("deleted views are : " + deletedViews)
    deletedViews.foreach {
      item =>
        val viewActor = viewActors(item)
        val pnfs = new PageNotFoundGETViewServer
        viewActor ! HotSwap(pnfs)
    }
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
