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

package com.bheap.silk.transformer

import scala.xml.{XML => ScalaXML}
import com.codecommit.antixml._

import java.io.File

import org.fusesource.scalate.scuery.Transformer

import com.bheap.silk.datasource.Datasource
import com.bheap.silk.utils.{Config, XML}

/** Injects components.
  *
  * Does a lookup and retrieves component content.
  *
  * @author <a href="mailto:ross@bheap.co.uk">rossputin</a>
  * @since 1.0 */
object ComponentTransformer {

  import Config._
  import XML._

  /** Transform components.
    *
    * First search for a local component, then a core component, finally
    * default to the missing-component. 
    *
    * @param xml the content to be transformed */
  // @todo currently hardcoded to only deal with div and span, review draft.. should Silk do more ?
  def transformComponents(xml: Elem) = {
    val divCompsTransformed = seekAndReplace(xml, 'div).head.asInstanceOf[Elem]
    seekAndReplace(divCompsTransformed, 'span)
  }

  /** Search and replace a Silk components with a given element name.
    *
    * Note we may or may be dealing with a dynamic component.  If we are
    * we will leverage the DynamicComponentTransformer to inject datasource
    * content.
    *
    * @param xml the content to be transformed
    * @param sym the element type to be searched on 'span' or 'div'
    * @return content with all instances of the referenced components replaced */
  def seekAndReplace(xml: Elem, sym: Symbol) = {
    val compsReplace = findElements(xml, sym, "silk-component") map {
      comp =>
        val compDetails = getComponentDetails(comp.attrs("id"))
        findElements(lookupComponent(compDetails), sym, "silk-component").head
    }
    compsReplace.unselect.unselect
  }

  /** Return a [[ComponentDetails]] given a component id.
    *
    * @param id the component id ie 'silk-component:some/path/name:date/timestamp' */
  // @todo make this functional, this is temporary and horrible code
  def getComponentDetails(id: String) = {
    val cIdBits = id.split(":")
    val cPathBits = cIdBits(1)
    val cDatasourceBits: Option[String] = if (cIdBits.size > 2) Some(cIdBits(2)) else None
    val cPath: Option[String] = if (cPathBits.contains(fs)) Some(cPathBits.split(fs).head) else None
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

  /** Return retrieved component content given [[ComponentDetails]].
    *
    * @param comp ComponentDetails */
  // @todo rudimentary draft only, ugly and makes assumptions about package, version and theme
  def lookupComponent(comp: ComponentDetails) = {
    val localComp = new File(userDirStr + fs + "component" + fs + comp.path + fs + comp.name + ".html")
    val coreCompStr = compStr + fs + corePkgStr + fs + comp.name + fs + "0.1.0" + fs + comp.name + ".html"
    val coreComp = new File(coreCompStr)
    if (localComp.exists) {
      ScalaXML.loadFile(localComp.toString).convert
    } else if (coreComp.exists) {
      ScalaXML.loadFile(coreCompStr).convert
    } else {
      val compBaseName = "component-missing"
      val theme = "none" //dnaConfig.getString("site-prototype.theme")
      val compName = if (theme == "none") compBaseName else compBaseName + "-" + theme
      ScalaXML.loadFile(compStr + fs + corePkgStr + fs + compName + fs + "0.1.0" + fs + compName + ".html").convert
    }
  }
}

/** Define the details of a component.
  *
  * @param path location of component
  * @param name name of component
  * @param dsFilter optional datasource filter
  * @param dsSource optional datasource source ie 'bheap'
  * @param dsSection optional datasource section ie 'news' */
case class ComponentDetails(path: String, name: String, dsFilter: Option[String], dsSource: Option[String], dsSection: Option[String])
