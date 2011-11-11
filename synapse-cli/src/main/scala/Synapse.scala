package com.bheap.synapse.interface

import java.io.File

import scopt._

import com.bheap.synapse.application.Application
import com.bheap.synapse.tools.{GiftWrap, Preview}

object Synapse {
  def main(args: Array[String]) {

    val tasks = "(create|build|run|preview-start)"

    var config = new Config()

    val parser = new OptionParser("synapse") {
      opt("t", "task", "task is a string property " + tasks, {t: String => config.task = Some(t)})
    }

    if (parser.parse(args)) {
      config.task.get match {
        case "create" =>
          val componentDir = new File(System.getProperty("user.dir") + "/component")
          val resourceDir = new File(System.getProperty("user.dir") + "/resource")
          val siteDir = new File(System.getProperty("user.dir") + "/site")
          val templateDir = new File(System.getProperty("user.dir") + "/template")
          val viewDir = new File(System.getProperty("user.dir") + "/view")
          if(!(componentDir).exists()) componentDir.mkdir
          if(!(resourceDir).exists()) resourceDir.mkdir
          if(!(siteDir).exists()) siteDir.mkdir
          if(!(templateDir).exists()) templateDir.mkdir
          if(!(viewDir).exists()) viewDir.mkdir
          println("Synapse create complete")
        case "build" =>
          val gw = new GiftWrap("default.html", "html")
          gw.build
          println("Synapse build complete")
        case "run" =>
          val app = new Application
          app.initialise
          app.start
          println("Synapse is running")
        case "preview-start" =>
          println("Synapse preview is running in the background on path : " + System.getProperty("user.dir"))
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

case class Config(var task: Option[String] = None)
