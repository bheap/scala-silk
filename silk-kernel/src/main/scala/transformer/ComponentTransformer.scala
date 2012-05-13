/**
 * Copyright (C) 2011-2012 Bheap Ltd - http://www.bheap.co.uk.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.silkyweb.transformer

import scala.io.Source
import scala.xml.{XML => ScalaXML}

import com.codecommit.antixml._

import java.io.File

import javax.xml.stream.XMLStreamException

import org.fusesource.scalate.scuery.Transformer

import org.silkyweb.datasource.Datasource
import org.silkyweb.utils.{Config, XML => SilkXML}

/** Injects components.
  *
  * Does a lookup and retrieves component content.  Transforms dynamic
  * component datasource content where applicable.
  *
  * @author <a href="mailto:ross@bheap.co.uk">rossputin</a>
  * @since 1.0 */
object ComponentTransformer {

  import Config._
  import SilkXML._

  /** Transform components.
    *
    * First search for a local component, then a core component, finally
    * default to the missing-component.
    *
    * Note while we only specify div and span here, this coveres every possibility,
    * as later we grab the full contents inside a body tag of any given component. 
    *
    * @param xml the content to be transformed */
  // @todo when we want dynamic components we will need to move beyond div and span
  def transformComponents(xml: Elem) = {
    val divCompsTransformed = seekAndReplace(xml, 'div, "id").head.asInstanceOf[Elem]
    val unselected = seekAndReplace(divCompsTransformed, 'span, "id")
    unselected(0).asInstanceOf[Elem]
  }

  /** Search and replace Silk components with a given element name.
    *
    * Note we may or may not be dealing with a dynamic component.  If we are
    * we will leverage the DynamicComponentTransformer to inject datasource
    * content.
    *
    * @param xml the content to be transformed
    * @param elem the element type to be searched on 'span' or 'div'
    * @param attr the attribute to search against a given value
    * @return content with all instances of the referenced components replaced */
  def seekAndReplace(xml: Elem, elem: Symbol, attr: String) = {
    val compsReplace = findElements(xml, elem, attr, "silk-component") map {
      comp =>
        val compDetails = getComponentDetails(comp.attrs("id"))
        val componentSelection = (lookupComponent(compDetails) \\ "body" \ *).filter(_.isInstanceOf[Elem])
        val componentContent = componentSelection(0).asInstanceOf[Elem]

        (for {
          datasource <- compDetails.dsSource
          datasection <- compDetails.dsSection
          transformedComponent <- transformDynamicComponent(datasource, datasection, componentContent)
		    } yield transformedComponent) getOrElse componentContent
    }
    compsReplace.unselect.unselect
  }

  /** Return transformed dynamic component.
    *
    * @param datasource the component datasource
    * @param datasection the component datasection 
    * @param componentContent the dynamic component content pre-transformation */
  def transformDynamicComponent(datasource: String, datasection: String, componentContent: Elem) = {
    val data = (new Datasource).get(datasource + "/" + datasection)
    val dynCompTrans = new DynamicComponentTransformer(data.get.toString)
    val dynTransContent = dynCompTrans(ScalaXML.loadString(componentContent.toString))
    Some(dynTransContent.head.convert)
  }

  /** Return a [[org.silkyweb.transformer.ComponentDetails]] given a component id.
    *
    * @param id the component id ie 'silk-component:some/path/name:date/timestamp' */
  // @todo make this functional, this is temporary and horrible code
  def getComponentDetails(id: String) = {
    val cIdBits = id.split(":")
    val cPathBits = cIdBits(1)
    val cDatasourceBits: Option[String] = if (cIdBits.size > 2) Some(cIdBits(2)) else None
    val cPath: Option[String] = if (cPathBits.contains(fs)) Some(extractNestedPath(cPathBits)) else None
    val cName = if (cPathBits.contains(fs)) cPathBits.split(fs).last else cPathBits

    val dsFilter = if (cDatasourceBits.isDefined) {
      val cdb = cDatasourceBits.get
      if (cdb.count(_ == fsChar) > 1) Some(cdb.split(fs).head) else None
    } else {
      None
    }

    val dsSource = if (cDatasourceBits.isDefined) {
      val cdb = cDatasourceBits.get
      if (cdb.count(_ == fsChar) > 1) Some(cdb.split(fs).tail.head) else Some(cdb.split(fs).head)
    } else {
      None
    }

    val dsSection = if (cDatasourceBits.isDefined) {
      val cdb = cDatasourceBits.get
      Some(cdb.split(fs).last)
    } else {
      None
    }
    
    ComponentDetails(cPath getOrElse "", cName, dsFilter, dsSource, dsSection)
  }

  def extractNestedPath(path: String) = {
    path.split(fs).init.reduceLeft[String] { (acc, n)  =>
      acc + fs + n
    }
  }

  /** Return retrieved component content given [[org.silkyweb.transformer.ComponentDetails]].
    *
    * @param comp [[org.silkyweb.transformer.ComponentDetails]] */
  // @todo rudimentary draft only, ugly and makes assumptions about package, version and theme
  def lookupComponent(comp: ComponentDetails) = {
    val localCompStr = userDirStr + fs + "component" + fs + comp.path + fs + comp.name + ".html"
    val localComp = new File(localCompStr)
    val coreCompStr = compStr + fs + corePkgStr + fs + comp.name + fs + "0.1.0" + fs + comp.name + ".html"
    val coreComp = new File(coreCompStr)
    if (localComp.exists) {
      loadComponentSource(localCompStr).get
    } else if (coreComp.exists) {
      loadComponentSource(coreCompStr).get
    } else {
      val compBaseName = "component-missing"
      val theme = "none" //dnaConfig.getString("site-prototype.theme")
      val compName = if (theme == "none") compBaseName else compBaseName + "-" + theme
      loadComponentSource(compStr + fs + corePkgStr + fs + compName + fs + "0.1.0" + fs + compName + ".html").get
    }
  }

  def loadComponentSource(file: String): Option[Elem] = {
    try {
      Some(XML.fromSource(Source.fromFile(file)))
    } catch {
      case xse: XMLStreamException => 
        println("Sorry... something has gone wrong with a component : " + file)
        println("It is possible the component file is not valid (x)html")
        println("Please have a look at the message below and try to fix it.")
        println(xse.getMessage.split("Message: ")(1))
        System.exit(1)
        None
    }
  }
}

/** Define the details of a component.
  *
  * @param path location of component
  * @param name name of component
  * @param dsFilter optional datasource filter is 'latest' or 'ranked'
  * @param dsSource optional datasource source ie 'bheap'
  * @param dsSection optional datasource section ie 'news' */
case class ComponentDetails(path: String, name: String, dsFilter: Option[String], dsSource: Option[String], dsSection: Option[String])
