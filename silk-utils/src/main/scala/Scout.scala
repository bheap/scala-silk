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

package com.bheap.silk.utils

import scala.util.matching.Regex

import java.io.File

/** Includes a convenience recursive directory scan.
  *
  * Used to detect structure of site to spin.
  *
  * @author <a href="mailto:ross@bheap.co.uk">rossputin</a>
  * @since 1.0 */
// @todo rename this to something useful
// @todo probably roll in the directory path depth detection currently entangled in URIAttributeRewriter
object Scout {

  def getRecursiveFilesInDirectoryOfType(file: File, regex: Regex): List[File] = {
	  val these = file.listFiles.toList
	  val good = these.filter(item => regex.findFirstIn(item.getName).isDefined)
	  good ++ these.filter(_.isDirectory).flatMap(getRecursiveFilesInDirectoryOfType(_,regex)).toList
	}
}