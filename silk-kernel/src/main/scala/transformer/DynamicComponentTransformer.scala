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

package org.silkyweb.transformer

import org.fusesource.scalate.scuery.Transformer

/** Transforms a dynamic component.
  *
  * Will transform any component which has an associated datasource injecting
  * the value(s) computed and retrieved from that datasource.
  *
  * @author <a href="mailto:ross@bheap.co.uk">rossputin</a>
  * @since 1.0 */
// @todo deal with dynamic component set, currently hardcoded to simplest datasource
class DynamicComponentTransformer(data: String) extends Transformer {

  $("span").contents = data
}
