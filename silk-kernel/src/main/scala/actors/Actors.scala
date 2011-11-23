package com.bheap.silk.actors

import scala.actors.Actor

/** Defines our view rendering actors.
  *
  * Here rather than concurrency performance characteristics we are
  * interested in hot swap capabilitites to enable modification of what
  * our simple Silk filters render in terms of view.
  *
  * @author <a href="mailto:ross@bheap.co.uk">rossputin</a>
  * @since 1.0 */
class ViewServerActor extends Actor {
  def act = {
    println("starting server actor...")
    loop(new DefaultGETViewServer)
  }

  def loop(server: ViewServer) {
    react {
      case Render =>
        reply(server.render)
        loop(server)

      case HotSwap(newServer) =>
        println("hot swapping code...")
        loop(newServer)

      case _ => loop(server)
    }
  }
}
