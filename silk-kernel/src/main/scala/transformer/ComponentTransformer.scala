package com.bheap.silk.transformer

import scala.xml._

import java.io.File

import org.fusesource.scalate.scuery.Transformer

/** Injects components.
  *
  * Does a lookup and retrieves component content.
  *
  * @author <a href="mailto:ross@bheap.co.uk">rossputin</a>
  * @since 1.0 */
class ComponentTransformer(view: Node) extends Transformer {

  val viewDiv = (view \\ "div").filter(item => (item \ "@id").toString.contains("silk-component"))
  viewDiv.foreach {
    comp => 
      val compStruct = (comp \ "@id")(0).toString
      val cPathBits = compStruct.split(":").last.split("/")
      val cPath = cPathBits.head
      val cName = cPathBits.last
      // @todo use path independent separator
      // @todo first try for a local component-missing
      val localComp = new File(System.getProperty("user.dir") + "/component/" + cPath + "/" + cName + ".html")
      val compXML = if (localComp.exists) {
        XML.loadFile(System.getProperty("user.dir") + "/component/" + cPath + "/" + cName + ".html")
      } else {
        XML.loadFile(System.getProperty("user.home") + "/.silk/repositories/component/com/bheap/silk/component-missing/0.1.0/component-missing.html")
      }
      val compDiv = (compXML \\ "div").find(item => (comp \ "@id").text == compStruct) 
      $("div#" + compStruct.replaceAll(":", "").replaceAll("/", "")).contents = compDiv.get
  }
}