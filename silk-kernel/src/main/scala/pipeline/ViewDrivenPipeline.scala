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

package org.silkyweb.pipeline

import scala.io.Source
import scala.xml.{XML => ScalaXML}

import com.codecommit.antixml._

import java.io.{File, FileWriter}

import com.bheap.scalautils.FileUtils._

import org.silkyweb.generator.{PathPreservingFileSourceGenerator => Generator}
import org.silkyweb.serialiser.Serialiser
import org.silkyweb.transformer.{ComponentTransformer, AntiXMLElemScriptTransformer, TemplateTransformer, URIAttributeTransformer}
import org.silkyweb.utils.{Bundler, Config, MalformedSourceException, XML => SXML}

/** Controls manipulation and representation of your site content.
  *
  * Conventions for configuration are defined in top level .silk directory.
  *
  * @author <a href="mailto:ross@bheap.co.uk">rossputin</a>
  * @since 1.0 */
object ViewDrivenPipeline {

  val userDir = new File(System.getProperty("user.dir"))
  val viewDir = new File(userDir, "view")
  val siteDir = new File(userDir, "site")

  /** Execute our pipeline.
    *
    * Always generate -> transform -> serialise. 
    * Note Generate leverages a default data medium, in this case the default of XML. */
  def process {
    val generated = Generator.generateFromXHTML(viewDir)
    generated foreach {
      viewFile =>
        val view = unpackSourceState(SXML.loadResource(viewFile))//XML.fromSource(Source.fromFile(viewFile))
        val transformedToTemplateWrapped = unpackSourceState(TemplateTransformer.transformTemplateWrapped(view)) //TemplateTransformer.transformTemplateWrapped(view).get
        val transformedToComponentInjected = ComponentTransformer.transformComponents(transformedToTemplateWrapped)
        val transformedToURIAttributeRewritten = rewriteAttributes(viewFile, transformedToComponentInjected)
        val transformedToSaneScript = AntiXMLElemScriptTransformer(transformedToURIAttributeRewritten)
        val serialisedToHtml5 = Serialiser.serialiseToHtml5(transformedToSaneScript)
        writeView(viewFile, serialisedToHtml5)
        Bundler.bundle(new File(userDir, "resource"), new File(siteDir, "resource"))
        Bundler.bundle(new File(userDir, "meta"), siteDir)
    }
  }

  def unpackSourceState(wrappedState: Either[Throwable, Elem]): Elem = {
    wrappedState match {
      case Right(data) => data
      case Left(error) =>
        error match {
          case x: MalformedSourceException =>
            println("Sorry, something has gone wrong with one of your source files : " + x.sourcefile)
            println("The error is listed below.")
            println(x.message)
            System.exit(1)
            <end />.convert
        }
    }
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
    val converted = imageTransformed.convert
    converted(0).asInstanceOf[Elem]
  }

  /** Write the generated artifacts to file after processing. */
  def writeView(file: File, view: String) {
    val fPath = new File(file.getParent.replace(Config.fs + "view", Config.fs + "site"))
    if (!fPath.exists) new File(file.getParent.replace(Config.fs + "view", Config.fs + "site")).mkdirs
    writeFileWithFlush((new File(fPath, file.getName)).toString, view)
  }
}
