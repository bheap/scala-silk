package com.bheap.silk.transformer

import scala.xml._
import scala.xml.transform._

/** Transforms content in script tags ready for browser consumption.
  *
  * Partially ported from some prototype code by <a href="mailto:barrie@bheap.co.uk">barrie@bheap.co.uk</a>.
  *
  * @author <a href="mailto:ross@bheap.co.uk">rossputin</a>
  * @since 1.0 */
object NestedElementRewriteRule extends RewriteRule {
  override def transform(n: Node): Seq[Node] = n match {
    case Elem(prefix, "script", attribs, scope, _*)  =>
      Unparsed(Elem(prefix, "script", attribs, scope, Unparsed(n.text.replace("&lt;", "<"))).toString)
    case other => other
  }
}

object NestedElementRuleTransformer extends RuleTransformer(NestedElementRewriteRule)

object ParentElementRewriteRule extends RewriteRule {
  override def transform(n: Node): Seq[Node] = n match {
    case sn @ Elem(_, "body", _, _, _*) => NestedElementRuleTransformer(sn)
    case other => other
  }
}

object ScriptTransformer extends RuleTransformer(ParentElementRewriteRule)
