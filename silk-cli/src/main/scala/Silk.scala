package com.bheap.silk.interface

import scala.xml._

import java.io.File

import scopt._

import com.bheap.silk.application.Application
import com.bheap.silk.tools.{GiftWrap, Preview}
import com.bheap.silk.utils.SilkBundle._

object Silk {
  def main(args: Array[String]) {

    val tasks = "(update|sites|site-install|clone|spin|run|preview-start)"

    var config = new Config()

    val parser = new OptionParser("silk") {
      opt("t", "task", "task is a string property " + tasks, {t: String => config.task = Some(t)})
      argOpt("<prototype>", "a prototype site", {ps: String => config.prototypeSite = Some(ps)})
    }

    if (parser.parse(args)) {
      config.task.get match {
        case "clone" =>
          val silkDir = System.getProperty("user.home") + "/.silk"
          val prototypeSiteDir = silkDir + "/repositories/site-prototype"
          val packageDir = "/com/bheap/silk"
          if ((new File(silkDir)).exists) {
            if (config.prototypeSite.isDefined && (new File(prototypeSiteDir + packageDir + "/" + config.prototypeSite.getOrElse("noooooo")).exists)) {
              val selectedProtoSite = config.prototypeSite.get
              println("Cloning from prototype site : " + selectedProtoSite + "...")
              val prototypeSite = prototypeSiteDir + "/com/bheap/silk/" + selectedProtoSite
              val silkVersion = "0.1.0"
              bundle(new File(prototypeSite + "/" + silkVersion), new File(System.getProperty("user.dir")))
              println("Silk clone complete")
            } else println("No prototype-site found with that id, please run silk prototype-site --list")
          } else println("Please run silk update, there are no prototype-sites on your system")
        case "site-install" =>
          val silkDir = System.getProperty("user.home") + "/.silk"
          val prototypeSiteDir = silkDir + "/repositories/site-prototype"
          val dnaXml = XML.loadFile(System.getProperty("user.dir") + "/.dna/dna.xml")
          val pkg = (dnaXml \\ "package").text.replace(".", "/")
          val id = (dnaXml \\ "id").text
          val silkVersion = (dnaXml \\ "silk-version").text
          println("Installing site prototype : " + id)
          println("package is : " + pkg)
          println("silk version is : " + silkVersion)
          val specificSP = new File(prototypeSiteDir + "/" + pkg + "/" + id + "/" + silkVersion)
          if (specificSP.exists) specificSP.delete
          specificSP.mkdirs
          bundle(new File(System.getProperty("user.dir")), specificSP)
          println("Silk site install complete")
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
