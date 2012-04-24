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

package org.silkyweb.interface

import scala.xml._

import java.io.File

import scopt._

import org.silkyweb.pipeline.ViewDrivenPipeline
import org.silkyweb.utils.{Bundler, Config => SilkConfig, Scout}

/** The CLI.
  *
  * An intuitive, simple CLI.
  *
  * @author <a href="mailto:ross@bheap.co.uk">rossputin</a>
  * @since 1.0 */
// @todo use package and version capabilities to enable community site prototypes and components
object Silk {

  import SilkConfig._

  def main(args: Array[String]) {

    println("Version : " + Info.version)

    val tasks = "(update|sites|site-clone|site-install|components|component-clone|component-install|spin)"

    var config = Config()

    val parser = new OptionParser("silk") {
      opt("t", "task", "task is a string property " + tasks, {t: String => config.task = Some(t)})
      argOpt("<prototype>", "a site or component prototype", {ps: String => config.prototype = Some(ps)})
    }

    if (parser.parse(args)) {
      config.task.get match {
        case "site-clone" => artifactClone(config.prototype, "site-prototype", siteProtoDir)
        case "site-install" => artifactInstall("site-prototype", siteProtoDir)
        case "component-clone" => artifactClone(config.prototype, "component", compDir)
        case "component-install" => artifactInstall("component", compDir)
        case "spin" => spin
        case _ => println("Sorry, not a valid action, please try " + tasks)
      }
    } else {
      println("that was bad okaaaayyyyy ?")
    }
  }

  /** Clone an artifact.
    *
    * Currently either a Site Prototype or a Component.
    *
    * @param prototype Option[String] an id for a site-prototype or component
    * @param artifactName String either 'site-prototype' or 'component'
    * @param artifactBase File a root directory */
  def artifactClone(prototype: Option[String], artifactName: String, artifactBase: File) {
    if (silkHomeDir.exists) {
      val projectId = prototype.getOrElse("nooo")
      var projects = getProjectsById(artifactBase, artifactName, projectId)
      if (projects.size > 0) {
        val packages = projects.map(_.pkg).distinct
        if (packages.size > 1) {
          val selectedPackage = userSelected(packages, projectId, "package")
          projects = projects.filter(_.pkg == selectedPackage)
        }
        val selectedVersion = autoSelectedVersion(projects.map(_.silkVersion).distinct, projectId)
        projects = projects.filter(_.silkVersion == selectedVersion)
        if (artifactName.equals("site-prototype")) {
          if (!localSilkConfigDir.exists) localSilkConfigDir.mkdir
          Bundler.bundleFile(masterSilkConfig, localSilkConfig)
        }
        Bundler.bundle(projects.head.baseDir, userDir)
        println("Silk %s : %s clone complete".format(artifactName, prototype.get))
      } else println("Sorry, no artifact found with that id")
    } else println("Please run silk update, your system is not setup properly")
  }


  /** User selects from a list of artifact details (package, version).
    *
    * @param choices List[String] list of choices given to user
    * @param projectId String display the projectId to user
    * @param selectionType String display the selectionType to user (package, version)
    * @return String from the list of choices */
  def userSelected(choices: List[String], projectId: String, selectionType: String): String = {
    println("The following %ss exist for artifact '%s'".format(selectionType, projectId))
    for(i <- 0 to choices.size -1) {
      println("\t%d) %s".format(i+1, choices(i)))
    }
    var i = -1
    while (i < 1 || i > choices.size) {
      try {
        println("Please specify the number of the %s you wish to clone: ".format(selectionType))
        i = readInt 
        if (i > 0 && i > choices.size) 
          println("Ooops please pick a number between 1 and  %d.".format(choices.size))
      } catch {
        case _ => println("Thats an invalid character, please try again.")
      }
    }
    choices(i - 1)
  }

  /** Determines silk version. Warns if component or user's silk version are out of date. 
    *
    * @param versions List[String] list of versions
    * @param projectId String display the projectId to user
    * @return String from the list of choices */
  def autoSelectedVersion(versions: List[String], projectId: String): String = {
    var version: String = versions.sortBy(s => s).last
    val runningSilkVersion = Info.version.split("-")(0) // Strip SNAPSHOT, ALPHA, BETA etc
    if (version < runningSilkVersion) {
      userCanAbortClone("WARNING: '%s' was built with an older version (%s), so proposal needs to be updated too.".format(projectId, version))
    } else if (version > runningSilkVersion) {
      userCanAbortClone("WARNING: '%s' was built with a newer version (%s), so proposal needs to be updated too. Please get the latest version from http://www.silkyweb.org.".format(projectId, version))
      if (versions.size > 1)
        version = userSelected(versions, projectId, "version")
    }
    version
  }

  /** Warns and allows user abort the clone.
    *
    * @param warningMsg String the warning */
  def userCanAbortClone(warningMsg: String) {
    println(warningMsg)
    println("Do you still wish to clone (y/n): ") 
    if (!readBoolean) sys.exit(0)
  }

  /** Install an artifact.
    *
    * Currently either a Site Prototype or a Component.
    *
    * @param artifactName String either 'site-prototype' or 'component'
    * @param artifactBase File a root directory */
  def artifactInstall(artifactName: String, artifactBase: File) {
    val pkg = dnaConfig.getString(artifactName + ".package").replace(".", fs)
    val id = dnaConfig.getString(artifactName + ".id")
    val silkVersion = dnaConfig.getString(artifactName + ".silk-version")
    println("package is : " + pkg)
    println("silk version is : " + silkVersion)
    val artifactFile = new File(artifactBase + fs + pkg + fs + id + fs + silkVersion)
    if (artifactFile.exists) artifactFile.delete
    artifactFile.mkdirs
    Bundler.bundle(userDir, artifactFile)
    println("Silk %s : %s install complete".format(artifactName, id))
  }

  /** Spin a silk site.
    *
    * Entails processing the configured pipeline. */
  // @todo determine pipeline from Silk config
  def spin {
    ViewDrivenPipeline.process
    println("Silk spin complete")
  }
}

case class Config(var task: Option[String] = None, var prototype: Option[String] = None)
