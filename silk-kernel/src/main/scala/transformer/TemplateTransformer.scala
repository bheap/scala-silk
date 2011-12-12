package com.bheap.silk.transformer

import scala.xml._

import org.fusesource.scalate.scuery.Transformer

/** Transforms a view into a template wrapped view.
  *
  * @author <a href="mailto:ross@bheap.co.uk">rossputin</a>
  * @since 1.0 */
class TemplateTransformer(viewNode: Node) extends Transformer {
  val contentDiv = (viewNode \\ "div").find(item => (item \ "@id").text == "silk-view")
  $("div#silk-template").contents = contentDiv.get
}