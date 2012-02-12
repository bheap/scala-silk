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

import scala.xml._

/** Prepares component id's to be accepted by CSS transformation.
  *
  * CSS transformations are restricted in terms of valid ID's.
  *
  * @author <a href="mailto:ross@bheap.co.uk">rossputin</a>
  * @since 1.0 */
// @todo oh god this is horrible, I have had no time but promise to fix it, was I mad.. or just tired beyond belief ?
object ComponentIdTransformer {
	
  // load file into string, parse Silk component attribute values ready for css transformation
  def diluteSilkComponents(node: Node) = {
    val content = node.toString
    val r = """(\"silk-component:[/a-z-A-Z_0-9:]+\")""".r
    val matchMap = r.findAllIn(content).toList.map(item => Map(item -> item.replaceAll(":", "").replaceAll("/", "")))
    // @todo iterate over the items in the map applying each replace based on them within the content string
    val firstP = if (matchMap.size > 0) Some(content.replace(matchMap.head.keySet.head, matchMap.head.values.head)) else None
    val secondP = if (matchMap.size > 1) Some(firstP.get.replace(matchMap.last.keySet.head, matchMap.last.values.head)) else None
    val parsedStr = 
      if (secondP.isDefined) secondP.get
      else if (firstP.isDefined) firstP.get
      else content
    XML.loadString(parsedStr)
  }
}
