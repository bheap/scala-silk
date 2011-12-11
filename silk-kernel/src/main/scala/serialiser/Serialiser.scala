package com.bheap.silk.serialiser

import scala.xml._

/** Serialise to HTML5.
  *
  * Done safely with XHTML.
  *
  * @author <a href="mailto:ross@bheap.co.uk">rossputin</a>
  * @since 1.0 */
object Serialiser {

  def serialiseToHTML5(node: Node) = {
    Xhtml.toXhtml(node).
      replace("<html>", "<!doctype html><html>")
  }
}
