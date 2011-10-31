package com.bheap.synapse.view

import scala.io.Source

import com.codecommit.antixml._

class XmlView(viewFile: String) {
  val view = XML fromSource (Source fromURL ("file:" + System.getProperty("user.home") + "/.synapse/sites/" + System.getProperty("ss") + "/views/" + viewFile))
}
