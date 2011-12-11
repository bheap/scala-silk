package com.bheap.silk.pipeline

import scala.xml._

import java.io.File

import com.bheap.silk.generator.PathPreservingFileSourceGenerator._
import com.bheap.silk.transformer.ComponentIdTransformer._
import com.bheap.silk.transformer.{ComponentTransformer, TemplateTransformer, URIAttributeTransformer}

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
  val templateDir = new File(userDir, "template")

  // execute our pipeline
  def process {
    val gen = generate
    val componentsTransformed = transformComponents(gen)
    val templatedViewsTransformed = transformTemplatedViews(componentsTransformed)
    println("templated is : " + templatedViewsTransformed)
  }

  // read in the view(s)
  // @todo make conditional based on mimetype
  def generate = {
    generateFromXHTML(viewDir)
  }

  def transformComponents(views: List[Tuple2[File, Node]]) = {
    views.map {
      view =>
        val cTransformer = new ComponentTransformer(view._2)
        (view._1, cTransformer(diluteSilkComponents(view._2))(0))
    }
  }

  def transformTemplatedViews(views: List[Tuple2[File, Node]]) = {
    val templateXml = XML.loadFile(new File(templateDir, "default.html"))
    views.map {
      view =>
        // @todo enable template defined in view without polluting semantic meaning
        val templateTransformer = new TemplateTransformer(view._2)
        val templateTransformed = templateTransformer(templateXml)

        val anchorUAT = new URIAttributeTransformer("a", "href", view._1)
		    val anchorTransformed = anchorUAT(templateTransformed)

        val linkUAT = new URIAttributeTransformer("link", "href", view._1)
        val linkTransformed = linkUAT(anchorTransformed)

        val scriptUAT = new URIAttributeTransformer("script", "src", view._1)
        val scriptTransformed = scriptUAT(linkTransformed)

        val imageUAT = new URIAttributeTransformer("img", "src", view._1)
        val imageTransformed = imageUAT(scriptTransformed)
        (view._1, imageTransformed)
    }
  }
}
