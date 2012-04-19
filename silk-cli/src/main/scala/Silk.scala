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
import org.silkyweb.utils.{Bundler, Config => SilkConfig}

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
        case "spin" => spin(config.prototype)
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
      if (new File(artifactBase + fs + corePkgStr + fs + prototype.getOrElse("nooo")).exists) {
        if (artifactName.equals("site-prototype")) {
          if (!localSilkConfigDir.exists) localSilkConfigDir.mkdir
          Bundler.bundleFile(masterSilkConfig, localSilkConfig)
        }
        Bundler.bundle(new File(artifactBase + fs + corePkgStr + fs + prototype.get + fs + "0.1.0"), userDir)
        println("Silk %s : %s clone complete".format(artifactName, prototype.get))
      } else println("Sorry, no artifact found with that id")
    } else println("Please run silk update, your system is not setup properly")
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
  def spin(prototype: Option[String]) {
	  val userDir = new File(prototype.getOrElse(System.getProperty("user.dir")))
    ViewDrivenPipeline.process(userDir)
    println("Silk spin complete")
  }
}

case class Config(var task: Option[String] = None, var prototype: Option[String] = None)
