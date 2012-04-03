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

package org.silkyweb.utils

import com.codecommit.antixml._

/** Some handy utilities for querying and manipulating antixml.
  *
  * @author <a href="mailto:ross@bheap.co.uk">rossputin</a>
  * @since 1.0 */
object XML {

  /** Searches an XML block for elements with a given name and a given id. */
  def findElements(xml: Elem, sym: Symbol, sig: String) = {
    xml \\ sym select Selector {case item: Elem if verifyAttribute(item.attrs, sig) => item }
  }

  /** A safe check for an attribute value. */
  def verifyAttribute(att: Attributes, sig: String) = {
    if (att.contains("id")) {
	    att("id").contains(sig)
    } else false
  }
}
