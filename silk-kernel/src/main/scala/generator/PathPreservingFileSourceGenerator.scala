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

package com.bheap.silk.generator

import scala.util.matching.Regex
import scala.xml._

import java.io.File

import com.bheap.silk.utils.SilkScout

/** Defines some generators.
  *
  * Source different contents and convert them to something Silk can work with.
  *
  * @author <a href="mailto:ross@bheap.co.uk">rossputin</a>
  * @since 1.0 */
object PathPreservingFileSourceGenerator {

  def generateFromXHTML(source: File) = {
    generateXmlFromFileSource(source, """.*\.html$""".r)
  }

  def generateXmlFromFileSource(source: File, mimeType: Regex) = {
    SilkScout.getRecursiveFilesInDirectoryOfType(source, mimeType).map {
      item =>
        val viewXML = XML.loadFile(item)
        (item, viewXML)
    }
  }
}
