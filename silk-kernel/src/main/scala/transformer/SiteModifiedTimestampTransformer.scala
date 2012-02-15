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

import org.fusesource.scalate.scuery.Transformer

/** Transforms a site-modified-timestamp component.
  *
  * This is localised so we are dealing with a small encapsulated fragment at this point, hence the
  * use of a simple span in this case.
  *
  * @author <a href="mailto:ross@bheap.co.uk">rossputin</a>
  * @since 1.0 */
// @todo not happy with this existing as just another component, migrate to components folder ?
class SiteModifiedTimestampTransformer(data: String) extends Transformer {
  $("span").contents = data
}
