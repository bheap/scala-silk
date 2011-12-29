package com.bheap.silk.transformer

import scala.xml._

import org.fusesource.scalate.scuery.Transformer

/** Transforms a site-modified-timestamp component.
  *
  * @author <a href="mailto:ross@bheap.co.uk">rossputin</a>
  * @since 1.0 */
// @todo not happy with this existing as just another component, migrate to components folder ?
class SiteModifiedTimestampTransformer(data: String) extends Transformer {
  $("div span").contents = data
}
