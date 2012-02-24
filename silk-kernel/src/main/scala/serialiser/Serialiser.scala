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

package com.bheap.silk.serialiser

//import scala.xml._

import com.codecommit.antixml._

import java.io.{File, FileWriter}

/** Serialise to HTML5.
  *
  * Done safely with XHTML.
  *
  * @author <a href="mailto:ross@bheap.co.uk">rossputin</a>
  * @since 1.0 */
// @todo enable conversion between anti-xml and scala-xml to leverage xhtml if required for some sites
object Serialiser {

  def serialiseToHtml5(view: Elem) = {
    "<!doctype html>" + view.toString
    //Xhtml.toXhtml(node).replace("<html>", "<!doctype html><html>")
  }

  /*def serialiseToHtml5WithIE(node: Node) = {
    Xhtml.toXhtml(node)
      .replace("<html>", "<!doctype html><!--[if lt IE 7 ]><html class=\"no-js ie6\" lang=\"en\"><![endif]--><!--[if IE 7 ]><html class=\"no-js ie7\" lang=\"en\"><![endif]--><!--[if IE 8 ]><html class=\"no-js ie8\" lang=\"en\"><![endif]--><!--[if (gte IE 9)|!(IE)]><!--><html class=\"no-js\" lang=\"en\"><!--<![endif]-->")
  }*/

  def writeView(file: File, view: String) {
    val fPath = new File(file.getParent.replace("/view", "/site"))
    if (!fPath.exists) new File(file.getParent.replace("/view", "/site")).mkdirs
    val out = new FileWriter(new File(fPath, file.getName))
    out.write(view)
    out.flush
    out.close
  }
}
