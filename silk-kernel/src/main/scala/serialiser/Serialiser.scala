package com.bheap.silk.serialiser

import scala.xml._

/** Serialise to HTML5.
  *
  * Done safely with XHTML.
  *
  * @author <a href="mailto:ross@bheap.co.uk">rossputin</a>
  * @since 1.0 */
object Serialiser {

  def serialiseToHtml5(node: Node) = {
    barrieTidy(Xhtml.toXhtml(node)
      .replace("<html>", "<!doctype html><html>"))
  }

  def serialiseToHtml5WithIE(node: Node) = {
    barrieTidy(Xhtml.toXhtml(node)
      .replace("<html>", "<!doctype html><!--[if lt IE 7 ]><html class=\"no-js ie6\" lang=\"en\"><![endif]--><!--[if IE 7 ]><html class=\"no-js ie7\" lang=\"en\"><![endif]--><!--[if IE 8 ]><html class=\"no-js ie8\" lang=\"en\"><![endif]--><!--[if (gte IE 9)|!(IE)]><!--><html class=\"no-js\" lang=\"en\"><!--<![endif]-->"))
  }

  // @todo rename this once I found out what Barrie intended
  def barrieTidy(content: String) = {
    content
      .replace("&lt;", "<")
      .replace("&gt;", ">")
      .replace("&apos;", "'")
      .replace("&quot;", "\"")
      .replace("/&amp;", "&")
  }
}
