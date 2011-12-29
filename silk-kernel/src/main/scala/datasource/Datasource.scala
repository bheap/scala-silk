package com.bheap.silk.datasource

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
