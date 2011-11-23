package com.bheap.silk.actors

/** Defines Silk actor driven rendering messages.
  *
  * @author <a href="mailto:ross@bheap.co.uk">rossputin</a>
  * @since 1.0 */
sealed abstract class SilkMessage
case object Render
case class HotSwap(s: ViewServer)
