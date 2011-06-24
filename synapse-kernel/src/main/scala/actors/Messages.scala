package com.bheap.synapse.actors

/** Defines Synapse actor driven rendering messages.
  *
  * @author <a href="mailto:ross@bheap.co.uk">rossputin</a>
  * @since 1.0 */
sealed abstract class SynapseMessage
case object Render
case class HotSwap(s: ViewServer)
