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
import scala.util.control.Exception._

import com.codecommit.antixml._

import java.io.{File, FileNotFoundException}

import org.silkyweb.utils.{Config, XML => SilkXML}

/** Transforms a view into a template wrapped view.
  *
  * @author <a href="mailto:ross@bheap.co.uk">rossputin</a>
  * @since 1.0 */
// @todo read the silk master config to drive details such as template mechanism
// @todo template information should be passed in and decoupled from this transformer
object TemplateTransformer {

  import Config._
  import SilkXML._

  // @todo mechanism will be defined from Silk config
  val templateDir = new File(userDir, "template")

  /** Wrap each view in the relevant template.
    *
    * This effectively finalises a high level model for each view. */
  // @todo rudimentary draft only, makes assumptions about package, version and theme
  def transformTemplateWrapped(xml: Elem) = {
    val template = lookupTemplate(xml) getOrElse lookupTemplateMissing
    val templateReplace = findElements(template, 'div, "id", "silk-template")
    val viewReplace = findElements(xml, 'div, "id", "silk-view")
    val unselected = templateReplace.updated(0, viewReplace.head).unselect.unselect
    unselected(0).asInstanceOf[Elem]
  }

  /** Retrieves a specified template.
    *
    * First we try locally, then Silk core. */
  def lookupTemplate(xml: Elem): Option[Elem] = {
    val specificTemplateDef = findElements(xml, 'meta, "name", "template")
    val specificTemplate = 
      if (specificTemplateDef nonEmpty) {
        val tName = specificTemplateDef(0).attrs("content")
        val coreTemplateFile = new File(templateStr + fs + corePkgStr + fs + tName + fs + "0.1.0" + fs + tName + ".html")
        loadTemplate(new File(templateDir, tName + ".html")) orElse {
          println("[Error] Template not found locally, searching in Silk core")
          loadTemplate(coreTemplateFile)
        }
      } else {
        loadTemplate(new File(templateDir, "default.html"))
      }
    specificTemplate
  }

  def lookupTemplateMissing = {
    println("[Error] No template found with the name you specified, using Silk core template-missing")
    val templateBaseName = "template-missing"
    val theme = "none" //dnaConfig.getString("site-prototype.theme")
    val templateName = if (theme == "none") templateBaseName else templateBaseName + "-" + theme
    XML.fromSource(Source.fromFile(templateStr + fs + corePkgStr + fs + templateName + fs + "0.1.0" + fs + templateName + ".html"))
  } 

  def loadTemplate(file: File): Option[Elem] = {
    val result: Either[Throwable, Elem] = 
      catching (classOf[FileNotFoundException]) either XML.fromSource(Source.fromFile(file))
    result match {
      case Left(error) => None
      case Right(data) => Some(data)
    }
  }
}
