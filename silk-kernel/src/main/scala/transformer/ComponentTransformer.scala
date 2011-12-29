package com.bheap.silk.transformer

import scala.xml._

import java.io.File

import org.fusesource.scalate.scuery.Transformer

import com.bheap.silk.datasource.Datasource
import com.bheap.silk.utils.SilkConfig

/** Injects components.
  *
  * Does a lookup and retrieves component content.
  *
  * @author <a href="mailto:ross@bheap.co.uk">rossputin</a>
  * @since 1.0 */
// @todo use path independent separator
// @todo rudimentary draft only, ugly and makes assumptions about package and version
// @todo transform component name element into relevant transformer somehow, currently hardcoded to SiteModified....
class ComponentTransformer(view: Node) extends Transformer {

  import SilkConfig._

  val viewDiv = (view \\ "div").filter(item => (item \ "@id").toString.contains("silk-component"))
  viewDiv.foreach {
    compItem =>
      val id = (compItem \ "@id")(0).toString
      val compDetails = getComponentDetails(id)

      val dataPath = (for {
          src <- compDetails.dsSource
          sct <- compDetails.dsSection
        }
	      yield {src + "/" + sct}) getOrElse ""

      val data = (new Datasource).get(dataPath)

      val compXML = lookupComponent(compDetails)
      
      val transplantXML = if (compDetails.dsSource.isDefined) {
        val constructor = classOf[SiteModifiedTimestampTransformer].getConstructors()(0)
        val args = Array[AnyRef](data.get.toString)
        val comp = constructor.newInstance(args:_*).asInstanceOf[Transformer]
        comp(compXML)
      } else compXML

      val compDiv = (transplantXML \\ "div").find(item => (compItem \ "@id").text == id) 
      $("div#" + id.replaceAll(":", "").replaceAll("/", "")).contents = compDiv.get
  }

  // @todo make this functional
  def getComponentDetails(id: String) = {
    val cIdBits = id.split(":")
    val cPathBits = cIdBits(1)
    val cDatasourceBits: Option[String] = if (cIdBits.size > 2) Some(cIdBits(2)) else None
    val cPath: Option[String] = if (cPathBits.contains("/")) Some(cPathBits.split("/").head + "/") else None
    val cName = if (cPathBits.contains("/")) cPathBits.split("/").last else cPathBits

    val dsFilter = if (cDatasourceBits.isDefined) {
      val cdb = cDatasourceBits.get
      if (cdb.count(_ == '/') > 1) Some(cdb.split("/").head) else None
    } else {
      None
    }

    val dsSource = if (cDatasourceBits.isDefined) {
      val cdb = cDatasourceBits.get
      if (cdb.count(_ == '/') > 1) Some(cdb.split("/").tail.head) else Some(cdb.split("/").head)
    } else {
      None
    }

    val dsSection = if (cDatasourceBits.isDefined) {
      val cdb = cDatasourceBits.get
      Some(cdb.split("/").last)
    } else {
      None
    }
    
    ComponentDetails(cPath getOrElse "", cName, dsFilter, dsSource, dsSection)
  }

  def lookupComponent(comp: ComponentDetails) = {
    val localComp = new File(userDirStr + "/component/" + comp.path + comp.name + ".html")
    val coreCompStr = userHomeDirStr + "/.silk/repositories/component/com/bheap/silk/" +
      comp.name + "/0.1.0/" + comp.name + ".html"
    val coreComp = new File(coreCompStr)
    if (localComp.exists) {
      XML.loadFile(userDirStr + "/component/" + comp.path + comp.name + ".html")
    } else if (coreComp.exists) {
      XML.loadFile(coreCompStr)
    } else {
      val compBaseName = "component-missing"
      val theme = dnaConfig.getString("site-prototype.theme")
      val compName = if (theme == "none") compBaseName else compBaseName + "-" + theme
      XML.loadFile(System.getProperty("user.home") + 
        "/.silk/repositories/component/com/bheap/silk/" + 
        compName + "/0.1.0/" + compName + ".html")
    }
  }
}

case class ComponentDetails(path: String, name: String, dsFilter: Option[String], dsSource: Option[String], dsSection: Option[String])
