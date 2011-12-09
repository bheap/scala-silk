package com.bheap.silk.pipeline

import scala.xml._

import java.io.File

import com.bheap.silk.generator.PathPreservingFileSourceGenerator._
import com.bheap.silk.transformer.ComponentIdTransformer._
import com.bheap.silk.transformer.ComponentTransformer

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

  // execute our pipeline
  def process {
    val gen = generate
    val componentsTransformed = transformComponents(gen)
    println("componentsTransformed is : " + componentsTransformed)
  }

  // read in the view(s)
  def generate = {
    // if mimetype (x)html
    generateFromXHTML(viewDir)
  }

  def transformComponents(views: List[Tuple2[File, Node]]) = {
    views.map {
      item =>
        val cTransformer = new ComponentTransformer(item._2)
        (item._1, cTransformer(diluteSilkComponents(item._2)))
    }
  }
}
