package com.bheap.silk.interface

import scala.xml._

import java.io.File

import scopt._

import com.bheap.silk.pipeline.ViewDrivenPipeline
import com.bheap.silk.tools.Preview
import com.bheap.silk.utils.SilkBundle._
import com.bheap.silk.utils.SilkConfig

/** The CLI.
  *
  * An intuitive, simple CLI.
  *
  * @author <a href="mailto:ross@bheap.co.uk">rossputin</a>
  * @since 1.0 */
object Silk {

  import SilkConfig._

  def main(args: Array[String]) {

    val tasks = "(update|sites|site-clone|site-install|components|component-clone|spin|preview-start)"

    var config = new Config()

    val parser = new OptionParser("silk") {
      opt("t", "task", "task is a string property " + tasks, {t: String => config.task = Some(t)})
      argOpt("<prototype>", "a site or component prototype", {ps: String => config.prototype = Some(ps)})
    }

    if (parser.parse(args)) {
      config.task.get match {
        case "site-clone" =>
          val silkDir = userHomeDirStr + "/.silk"
          val prototypeSiteDir = silkDir + "/repositories/site-prototype"
          val packageDir = "/com/bheap/silk"
          if ((new File(silkDir)).exists) {
            if (config.prototype.isDefined && (new File(prototypeSiteDir + packageDir + "/" + config.prototype.getOrElse("noooooo")).exists)) {
              val silkLocalDir = new File(userDir, ".silk")
              if (!silkLocalDir.exists) silkLocalDir.mkdir
              bundleFile(new File(silkHomeDir, "silk.conf"), new File(silkLocalDir, "silk.conf"))
              val selectedProtoSite = config.prototype.get
              println("Cloning from site prototype : " + selectedProtoSite + "...")
              val prototypeSite = prototypeSiteDir + "/com/bheap/silk/" + selectedProtoSite
              val silkVersion = "0.1.0"
              bundle(new File(prototypeSite + "/" + silkVersion), userDir)
              println("Silk site prototype clone complete")
            } else println("No site prototype found with that id, please run silk sites")
          } else println("Please run silk update, there are no site prototypes on your system")
        case "site-install" =>
          val silkDir = userHomeDirStr + "/.silk"
          val prototypeSiteDir = silkDir + "/repositories/site-prototype"
          val pkg = dnaConfig.getString("site-prototype.package").replace(".", "/")
          val id = dnaConfig.getString("site-prototype.id")
          val silkVersion = dnaConfig.getString("site-prototype.silk-version")
          println("Installing site prototype : " + id)
          println("package is : " + pkg)
          println("silk version is : " + silkVersion)
          val specificSP = new File(prototypeSiteDir + "/" + pkg + "/" + id + "/" + silkVersion)
          if (specificSP.exists) specificSP.delete
          specificSP.mkdirs
          bundle(userDir, specificSP)
          println("Silk site install complete")
        case "component-clone" =>
          val silkDir = userHomeDirStr + "/.silk"
          val componentDir = silkDir + "/repositories/component"
          val packageDir = "/com/bheap/silk"
          if ((new File(silkDir)).exists) {
            if (config.prototype.isDefined && (new File(componentDir + packageDir + "/" + config.prototype.getOrElse("noooooo")).exists)) {
              val silkLocalDir = new File(userDir, ".silk")
              if (!silkLocalDir.exists) silkLocalDir.mkdir
              bundleFile(new File(silkHomeDir, "silk.conf"), new File(silkLocalDir, "silk.conf"))
              val selectedProtoSite = config.prototype.get
              println("Cloning from component : " + selectedProtoSite + "...")
              val prototypeSite = componentDir + "/com/bheap/silk/" + selectedProtoSite
              val silkVersion = "0.1.0"
              bundle(new File(prototypeSite + "/" + silkVersion), userDir)
              println("Silk component clone complete")
            } else println("No component found with that id, please run silk components")
          } else println("Please run silk update, there are no components on your system")
        case "spin" =>
          ViewDrivenPipeline.process
          println("Silk spin complete")
        case "preview-start" =>
          println("Silk preview is running in the background on path : " + userDirStr)
          println("Please hit enter to continue...")
          val preview = new Preview(userDirStr)
        case _ =>
          println("Sorry, not a valid action, please try " + tasks)
      }
    } else {
      println("that was bad okaaaayyyyy ?")
    }
  }
}

case class Config(var task: Option[String] = None, var prototype: Option[String] = None)
