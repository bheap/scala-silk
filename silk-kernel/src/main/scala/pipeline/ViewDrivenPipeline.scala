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

package com.bheap.silk.pipeline

import scala.xml.{XML => ScalaXML}
import com.codecommit.antixml._

import java.io.{File, FileWriter}

import com.bheap.silk.generator.{PathPreservingFileSourceGenerator => Generator}
import com.bheap.silk.serialiser.Serialiser
import com.bheap.silk.transformer.{ComponentTransformer, ScriptTransformer, TemplateTransformer, URIAttributeTransformer}
import com.bheap.silk.utils.SilkBundle

/** Controls manipulation and representation of your site content.
  *
  * Conventions for configuration are defined in top level .silk directory.
  *
  * @author <a href="mailto:ross@bheap.co.uk">rossputin</a>
  * @since 1.0 */
// @todo read the silk master config to drive pipeline
object ViewDrivenPipeline {

  val userDir = new File(System.getProperty("user.dir"))
  val viewDir = new File(userDir, "view")
  val siteDir = new File(userDir, "site")

  /** Execute our pipeline.
    *
    * Always generate -> transform -> serialise. 
    * Note Generate leverages a default data medium, in this case the default of XML. */
  // @todo read transformation steps from Silk pipeline config
  def process {
    val generated = Generator.generateFromXHTML(viewDir)
    generated foreach {
      viewFile =>
        val view = ScalaXML.loadFile(viewFile).convert
        val transformedToTemplateWrapped = TemplateTransformer.transformTemplateWrapped(view)(0).asInstanceOf[Elem]
        val transformedToComponentInjected = ComponentTransformer.transformComponents(transformedToTemplateWrapped)(0).asInstanceOf[Elem]
        val transformedToURIAttributeRewritten = rewriteAttributes(viewFile, transformedToComponentInjected)(0).asInstanceOf[Elem]
        val serialisedToHtml5 = Serialiser.serialiseToHtml5(transformedToURIAttributeRewritten)
        writeView(viewFile, serialisedToHtml5)
        SilkBundle.bundle(new File(userDir, "resource"), new File(siteDir, "resource"))
		    SilkBundle.bundle(new File(userDir, "meta"), siteDir)
    }
    //*val scriptTransformed = transformScripts(templatedViewsTransformed)*/
  }

  /** Rewrite URI attributes for all relevant element types.
    *
    * Note this method handles all conversions between anti-xml and scala xml
    * required for scuery. */
  def rewriteAttributes(viewFile: File, xml: Elem) = {
    val anchorUAT = new URIAttributeTransformer("a", "href", viewFile)
    val anchorTransformed = anchorUAT(ScalaXML.loadString(xml.toString))
		val linkUAT = new URIAttributeTransformer("link", "href", viewFile)
    val linkTransformed = linkUAT(anchorTransformed)
    val scriptUAT = new URIAttributeTransformer("script", "src", viewFile)
    val scriptTransformed = scriptUAT(linkTransformed)
    val imageUAT = new URIAttributeTransformer("img", "src", viewFile)
    val imageTransformed = imageUAT(scriptTransformed)
    imageTransformed.convert
  }

  def writeView(file: File, view: String) {
    val fPath = new File(file.getParent.replace("/view", "/site"))
    if (!fPath.exists) new File(file.getParent.replace("/view", "/site")).mkdirs
    val out = new FileWriter(new File(fPath, file.getName))
    out.write(view)
    out.flush
    out.close
  }

  /*def transformScripts(views: List[Tuple2[File, Node]]) = {
    views.map {
      view =>
        (view._1, ScriptTransformer(view._2))
    }
  }*/
}
