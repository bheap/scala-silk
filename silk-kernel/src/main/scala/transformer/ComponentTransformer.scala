package com.bheap.silk.transformer

import scala.xml._

import java.io.File

import org.fusesource.scalate.scuery.Transformer

import com.bheap.silk.utils.SilkConfig

/** Injects components.
  *
  * Does a lookup and retrieves component content.
  *
  * @author <a href="mailto:ross@bheap.co.uk">rossputin</a>
  * @since 1.0 */
// @todo use path independent separator
// @todo first try for a local component-missing, then core, then missing
class ComponentTransformer(view: Node) extends Transformer {

  import SilkConfig._

  val viewDiv = (view \\ "div").filter(item => (item \ "@id").toString.contains("silk-component"))
  viewDiv.foreach {
    comp => 
      val compStruct = (comp \ "@id")(0).toString
      val cPathBits = compStruct.split(":").last.split("/")
      val cPath = cPathBits.head
      val cName = cPathBits.last
      val localComp = new File(userDirStr + "/component/" + cPath + "/" + cName + ".html")
      val compXML = if (localComp.exists) {
        XML.loadFile(userDirStr + "/component/" + cPath + "/" + cName + ".html")
      } else {
        val compBaseName = "component-missing"
        val theme = dnaConfig.getString("site-prototype.theme")
        val compName = if (theme == "none") compBaseName else compBaseName + "-" + theme
        XML.loadFile(System.getProperty("user.home") + 
          "/.silk/repositories/component/com/bheap/silk/" + 
          compName + "/0.1.0/" + compName + ".html")
      }
      val compDiv = (compXML \\ "div").find(item => (comp \ "@id").text == compStruct) 
      $("div#" + compStruct.replaceAll(":", "").replaceAll("/", "")).contents = compDiv.get
  }
}