package com.bheap.synapse.interface

import scopt._

import com.bheap.synapse.tools.GiftWrap

object Synapse {
  def main(args: Array[String]) {
    var config = new Config()

    val parser = new OptionParser("synapse") {
      opt("t", "task", "task is a string property (wrap|run)", {t: String => config.task = Some(t)})
    }

    if (parser.parse(args)) {
      config.task.get match {
        case "wrap" =>
          val gw = new GiftWrap("default.html", "html")
          gw.wrap
      }
    } else {
      println("that was bad okaaaayyyyy ?")
    }
  }
}

case class Config(var task: Option[String] = None)
