package com.bheap.synapse.actors

import com.bheap.synapse.view.XmlView

/** Provides default view rendering for our Synapse server actors.
  *
  * @author <a href="mailto:ross@bheap.co.uk">rossputin</a>
  * @since 1.0 */
trait ViewServer {

  /** Returns a default view rendering, should be overriden. */
  def render = <html><body><h1>Default view from Synapse view server</h1></body></html>.toString
}

/** Default GET view.
  *
  * Purely a nice safe way to start an actor before any useful view
  * functionality is passed in. */
class DefaultGETViewServer extends ViewServer

/** 404 GET view.
  *
  * Useful when we reload a site and views have been removed. */
class PageNotFoundGETViewServer extends ViewServer {
  override def render = <html><body><h1>Page not found.  Synapse can not find the page you are looking for.</h1></body></html>.toString
}

/** Enables GET views.
  *
  * @param view a path to a view within a site configuration */
class GETViewServer(viewPath: String) extends ViewServer {
  val view = new XmlView(viewPath).view  

  override def render = view.toString
}
