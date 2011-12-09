package com.bheap.silk.transformer

import scala.xml._

/** Prepares component id's to be accepted by CSS transformation.
  *
  * CSS transformations are restricted in terms of valid ID's.
  *
  * @author <a href="mailto:ross@bheap.co.uk">rossputin</a>
  * @since 1.0 */
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
