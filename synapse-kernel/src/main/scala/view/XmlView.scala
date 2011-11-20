package com.bheap.synapse.view

import scala.io.Source
import scala.xml._

class XmlView(viewFile: String) {
  // @todo use path independent separator
  val view = XML.loadFile(System.getProperty("user.dir") + "/site/" + viewFile)
}
