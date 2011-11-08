package com.bheap.synapse.interface

import java.io.File

import scopt._

import com.bheap.synapse.application.Application
import com.bheap.synapse.tools.{GiftWrap, Preview}

object Synapse {
  def main(args: Array[String]) {
    var config = new Config()

    val parser = new OptionParser("synapse") {
      opt("t", "task", "task is a string property (create|build|run)", {t: String => config.task = Some(t)})
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
        case "build" =>
          val gw = new GiftWrap("default.html", "html")
          gw.build
        case "run" =>
          val app = new Application
          app.initialise
          app.start
        case n if n contains "preview-" =>
          println("initialising preview")
          println("n is : " + n)
          println("path is : " + n.split("preview-").last)
          val preview = new Preview(n.split("preview-").last)
        case _ =>
          println("Sorry, not a valid action, please try (create|build|run)")
      }
    } else {
      println("that was bad okaaaayyyyy ?")
    }
  }
}

case class Config(var task: Option[String] = None)
