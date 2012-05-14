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

import javax.xml.stream.XMLStreamException

import org.silkyweb.utils.MalformedSourceException
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
  def transformTemplateWrapped(xml: Elem): Either[Throwable, Elem] = {//Option[Elem] = {
    val template = lookupTemplate(xml)
    template match {
      case Right(data) =>
        val templateReplace = findElements(data, 'div, "id", "silk-view")
        val viewSelection = (xml \\ "body" \ *).filter(_.isInstanceOf[Elem])

        val viewReplace = viewSelection(0).asInstanceOf[Elem]
        val unselected = templateReplace.updated(0, viewReplace).unselect.unselect
        Right(unselected(0).asInstanceOf[Elem])
      case Left(error) =>
        Left(error)
    }
  }

  def lookupTemplate(xml: Elem): Either[Throwable, Elem] = {
    val specificTemplDef = findElements(xml, 'meta, "name", "template")
    val actualTempl = for (templEl <- specificTemplDef headOption) yield getSpecificTempl(templEl.attrs("content"))
    actualTempl getOrElse getDefaultTempl
  }

  def getSpecificTempl(templateName: String): Either[Throwable, Elem] = {
    val lookupFiles = List(
	    templateDir + fs + templateName + ".html",
	    templateStr + fs + corePkgStr + fs + templateName + fs + "0.1.0" + fs + templateName + ".html",
      prepareTemplateMissingPath
	  )
    lookupChainedSource(lookupFiles)
  }

  def getDefaultTempl: Either[Throwable, Elem] = {
    val lookupFiles = List(
      templateDir + fs + "default.html",
      prepareTemplateMissingPath
    )
    lookupChainedSource(lookupFiles)
  }

  // @todo rudimentary draft only, makes assumptions about package, version and theme
  def prepareTemplateMissingPath = {
    val templateBaseName = "template-missing"
    val theme = "none" //dnaConfig.getString("site-prototype.theme")
    val templateName = if (theme == "none") templateBaseName else templateBaseName + "-" + theme
    templateStr + fs + corePkgStr + fs + templateName + fs + "0.1.0" + fs + templateName + ".html"
  }
}
