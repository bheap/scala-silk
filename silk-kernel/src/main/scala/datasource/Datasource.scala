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

package org.silkyweb.datasource

import java.util.Date

/** Abstracts data for Silk.
  *
  * Path tells us where to get data from and how to treat it.
  * Note, data is returned in its raw form, i18n and l10n are factored in later.
  *
  * @author <a href="mailto:ross@bheap.co.uk">rossputin</a>
  * @since 1.0 */
// @todo expand on this to actually route data requirements
class Datasource {

  def get(path: String): Option[Any] = {
    if (path == "date/timestamp") Some(new java.util.Date) else None
  }
}
