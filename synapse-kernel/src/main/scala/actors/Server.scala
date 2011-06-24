package com.bheap.synapse.actors

/** Provides default view rendering for our Synapse server actors.
  *
  * @author <a href="mailto:ross@bheap.co.uk">rossputin</a>
  * @since 1.0 */
trait ViewServer {

  /** Returns a default view rendering, should be overriden. */
  def render = <html><body><h1>Default vew from Synapse view server</h1></body></html>.toString
}

/** Returns GET views. */
class GetViewServer extends ViewServer
