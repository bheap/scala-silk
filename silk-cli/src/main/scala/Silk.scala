package com.bheap.silk.interface

import java.io.File

import scopt._

import com.bheap.silk.application.Application
import com.bheap.silk.tools.{GiftWrap, Preview}
import com.bheap.silk.utils.SilkBundle._

object Silk {
  def main(args: Array[String]) {

    val tasks = "(clone|spin|run|preview-start)"

    var config = new Config()

    val parser = new OptionParser("silk") {
      opt("t", "task", "task is a string property " + tasks, {t: String => config.task = Some(t)})
      argOpt("<prototype>", "a prototype site", {ps: String => config.prototypeSite = Some(ps)})
    }

    if (parser.parse(args)) {
      config.task.get match {
        case "clone" =>
          val selectedProtoSite = config.prototypeSite getOrElse "default-empty"
          println("Cloning from prototype site : " + selectedProtoSite + "...")
          val prototypeSite = "/.silk/repositories/prototype-site/com/bheap/silk/" + selectedProtoSite
          val silkVersion = "0.1.0"
          bundle(new File(System.getProperty("user.home") + prototypeSite + "/" + silkVersion), new File(System.getProperty("user.dir")))
          println("Silk clone complete")
        case "spin" =>
          val gw = new GiftWrap("default.html", "html")
          gw.build
          println("Silk spin complete")
        case "run" =>
          val app = new Application
          app.initialise
          app.start
          println("Silk is running")
        case "preview-start" =>
          println("Silk preview is running in the background on path : " + System.getProperty("user.dir"))
          println("Please hit enter to continue...")
          val preview = new Preview(System.getProperty("user.dir"))
        case _ =>
          println("Sorry, not a valid action, please try " + tasks)
      }
    } else {
      println("that was bad okaaaayyyyy ?")
    }
  }
}

case class Config(var task: Option[String] = None, var prototypeSite: Option[String] = None)
