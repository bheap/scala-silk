package com.bheap.silk.interface

import scala.xml._

import java.io.File

import scopt._

import com.bheap.silk.pipeline.ViewDrivenPipeline
import com.bheap.silk.tools.Preview
import com.bheap.silk.utils.SilkBundle._

/** The CLI.
  *
  * An intuitive, simple CLI.
  *
  * @author <a href="mailto:ross@bheap.co.uk">rossputin</a>
  * @since 1.0 */
object Silk {

  val userHomeStr = System.getProperty("user.home")
  val userDirStr = System.getProperty("user.dir")
  val userDir = new File(userDirStr)

  def main(args: Array[String]) {

    val tasks = "(update|sites|site-install|clone|spin|preview-start)"

    var config = new Config()

    val parser = new OptionParser("silk") {
      opt("t", "task", "task is a string property " + tasks, {t: String => config.task = Some(t)})
      argOpt("<prototype>", "a prototype site", {ps: String => config.prototypeSite = Some(ps)})
    }

    if (parser.parse(args)) {
      config.task.get match {
        case "clone" =>
          val silkDir = userHomeStr + "/.silk"
          val prototypeSiteDir = silkDir + "/repositories/site-prototype"
          val packageDir = "/com/bheap/silk"
          if ((new File(silkDir)).exists) {
            if (config.prototypeSite.isDefined && (new File(prototypeSiteDir + packageDir + "/" + config.prototypeSite.getOrElse("noooooo")).exists)) {
              val selectedProtoSite = config.prototypeSite.get
              println("Cloning from prototype site : " + selectedProtoSite + "...")
              val prototypeSite = prototypeSiteDir + "/com/bheap/silk/" + selectedProtoSite
              val silkVersion = "0.1.0"
              bundle(new File(prototypeSite + "/" + silkVersion), userDir)
              println("Silk clone complete")
            } else println("No prototype-site found with that id, please run silk prototype-site --list")
          } else println("Please run silk update, there are no prototype-sites on your system")
        case "site-install" =>
          val silkDir = userHomeStr + "/.silk"
          val prototypeSiteDir = silkDir + "/repositories/site-prototype"
          val dnaXml = XML.loadFile(userDirStr + "/.dna/dna.xml")
          val pkg = (dnaXml \\ "package").text.replace(".", "/")
          val id = (dnaXml \\ "id").text
          val silkVersion = (dnaXml \\ "silk-version").text
          println("Installing site prototype : " + id)
          println("package is : " + pkg)
          println("silk version is : " + silkVersion)
          val specificSP = new File(prototypeSiteDir + "/" + pkg + "/" + id + "/" + silkVersion)
          if (specificSP.exists) specificSP.delete
          specificSP.mkdirs
          bundle(userDir, specificSP)
          println("Silk site install complete")
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

case class Config(var task: Option[String] = None, var prototypeSite: Option[String] = None)
