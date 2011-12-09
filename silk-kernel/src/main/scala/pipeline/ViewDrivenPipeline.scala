package com.bheap.silk.pipeline

import scala.xml._

import java.io.File

import com.bheap.silk.generator.PathPreservingFileSourceGenerator._
import com.bheap.silk.transformer.ComponentIdTransformer._
import com.bheap.silk.transformer.{ComponentTransformer, TemplateTransformer}

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
      item =>
        val cTransformer = new ComponentTransformer(item._2)
        (item._1, cTransformer(diluteSilkComponents(item._2))(0))
    }
  }

  def transformTemplatedViews(views: List[Tuple2[File, Node]]) = {
    val templateXml = XML.loadFile(new File(templateDir, "default.html"))
    views.map {
      item =>
        val templateTransformer = new TemplateTransformer(item._2)
        (item._1, templateTransformer(templateXml))
    }
  }
}
