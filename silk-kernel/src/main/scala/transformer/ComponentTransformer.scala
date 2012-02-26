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
import com.bheap.silk.utils.{SilkConfig, SilkXML}

/** Injects components.
  *
  * Does a lookup and retrieves component content.
  *
  * @author <a href="mailto:ross@bheap.co.uk">rossputin</a>
  * @since 1.0 */
// @todo use path independent separator
// @todo rudimentary draft only, ugly and makes assumptions about package and version
// @todo transform component name element into relevant transformer somehow, currently hardcoded to SiteModified....
// @todo remove duplication between div and span processing
object ComponentTransformer {

  import SilkConfig._
  import SilkXML._

  /** Transform components.
    *
    * First search for a local component, then a core component, finally
    * default to the missing-component. */
  // @todo currently hardcoded to only deal with div and span, review draft.. should Silk do more ?
  def transformComponents(xml: Elem) = {
    val divCompsTransformed = seekAndReplace(xml, 'div).head.asInstanceOf[Elem]
    seekAndReplace(divCompsTransformed, 'span)
  }

  /** Search and replace a Silk component with a given element name. */
  def seekAndReplace(xml: Elem, sym: Symbol) = {
    val compsReplace = findElements(xml, sym, "silk-component") map {
      comp =>
        val compDetails = getComponentDetails(comp.attrs("id"))
        findElements(lookupComponent(compDetails), sym, "silk-component").head
    }
    compsReplace.unselect.unselect
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
      ScalaXML.loadFile(userDirStr + "/component/" + comp.path + comp.name + ".html").convert
    } else if (coreComp.exists) {
      ScalaXML.loadFile(coreCompStr).convert
    } else {
      val compBaseName = "component-missing"
      val theme = "none" //dnaConfig.getString("site-prototype.theme")
      val compName = if (theme == "none") compBaseName else compBaseName + "-" + theme
      ScalaXML.loadFile(System.getProperty("user.home") + 
        "/.silk/repositories/component/com/bheap/silk/" + 
        compName + "/0.1.0/" + compName + ".html").convert
    }
  }
}

case class ComponentDetails(path: String, name: String, dsFilter: Option[String], dsSource: Option[String], dsSection: Option[String])
