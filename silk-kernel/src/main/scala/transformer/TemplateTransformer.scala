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

import com.bheap.silk.utils.{Config, XML}

/** Transforms a view into a template wrapped view.
  *
  * @author <a href="mailto:ross@bheap.co.uk">rossputin</a>
  * @since 1.0 */
// @todo read the silk master config to drive details such as template mechanism
// @todo template information should be passed in and decoupled from this transformer
object TemplateTransformer {

  import Config._
  import XML._

  // @todo mechanism will be defined from Silk config
  val templateDir = new File(userDir, "template")

  /** Wrap each view in the relevant template.
    *
    * This effectively finalises a high level model for each view. 
    *
    * Template rules follow inheritance and convention.  Check in order;
    * is a local template defined in the page (head furniture), is a
    * specific external template specified (head furniture), if not
    * fall back to the local default template, or the core default template.
    */
  // @todo create a core default template
  def transformTemplateWrapped(xml: Elem) = {
    val template = ScalaXML.loadFile(new File(templateDir, "default.html")).convert
    val templateReplace = findElements(template, 'div, "silk-template")
    val viewReplace = findElements(xml, 'div, "silk-view")
    templateReplace.updated(0, viewReplace.head).unselect.unselect
  }
}
