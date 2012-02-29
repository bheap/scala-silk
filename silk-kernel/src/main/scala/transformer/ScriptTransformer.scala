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
import scala.xml.transform._

/** Transforms content in script tags ready for browser consumption.
  *
  * The replace operation element of this code was ported from some prototype code by 
  * <a href="mailto:barrie@bheap.co.uk">barrie@bheap.co.uk</a>.
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
