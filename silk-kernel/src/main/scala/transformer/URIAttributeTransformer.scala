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

import java.io.File

import org.fusesource.scalate.scuery.Transformer

import com.bheap.io.PathUtils

/** Transforms the URI related attributes of content.
  *
  * Useful for rewriting links etc.
  *
  * @author <a href="mailto:ross@bheap.co.uk">rossputin</a>
  * @since 1.0 */
// @todo re-write to pass in a simple depth value, decouple from knowledge of file system
class URIAttributeTransformer(element: String, attribute: String, view: File) extends Transformer {
  $(element).selectiveAttribute(attribute) {
    n =>
      val currentHref = (n \ ("@" + attribute)).toString
      // perform my criteria checks here, and regex to determine depth of location in path
      if (currentHref.contains("http:") || currentHref.contains("mailto:") || currentHref.contains("https:") || currentHref.contains("feed:") || currentHref.startsWith("#")) currentHref
      else {
        val rootPath = System.getProperty("user.dir") + "/view/"
        val viewDepth = (view.toString.split(rootPath).last count (item => item == '/')) + 1
        val urlDepth = currentHref count (item => item == '/')
        if (viewDepth > 1) {
          val urlSubPath = if (urlDepth == 0) "" else {
            (new File(rootPath + currentHref)).getParentFile.toString.split(rootPath).last
          }
          val urlPath = new File(rootPath + urlSubPath)
          val pathDiff = PathUtils.relativize(view.getParentFile, urlPath)
          pathDiff + "/" + (new File(rootPath + currentHref)).getName
        } else {
          currentHref
        }
      }
  }
}
