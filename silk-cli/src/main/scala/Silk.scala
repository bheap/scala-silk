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

package com.bheap.silk.interface

import scala.xml._

import java.io.File

import scopt._

import com.bheap.silk.pipeline.ViewDrivenPipeline
import com.bheap.silk.utils.{Bundler, Config => SilkConfig}

/** The CLI.
  *
  * An intuitive, simple CLI.
  *
  * @author <a href="mailto:ross@bheap.co.uk">rossputin</a>
  * @since 1.0 */
// @todo refactoring required to de-dupe the clone functions
// @todo use package and version capabilities to enable community site prototypes and components
object Silk {

  import SilkConfig._

  def main(args: Array[String]) {

    val tasks = "(update|sites|site-clone|site-install|components|component-clone|component-install|spin)"

    var config = new Config()

    val parser = new OptionParser("silk") {
      opt("t", "task", "task is a string property " + tasks, {t: String => config.task = Some(t)})
      argOpt("<prototype>", "a site or component prototype", {ps: String => config.prototype = Some(ps)})
    }

    if (parser.parse(args)) {
      config.task.get match {
        case "site-clone" => siteClone(config.prototype)
        case "site-install" => artifactInstall("site-prototype", siteProtoDir)
        case "component-clone" => componentClone(config.prototype)
        case "component-install" => artifactInstall("component", compDir)
        case "spin" => spin
        case _ => println("Sorry, not a valid action, please try " + tasks)
      }
    } else {
      println("that was bad okaaaayyyyy ?")
    }
  }

  def siteClone(prototype: Option[String]) {
    if (silkHomeDir.exists) {
      if (new File(siteProtoDir,  corePkgStr + fs + prototype.getOrElse("nooo")).exists) {
        if (!localSilkConfigDir.exists) localSilkConfigDir.mkdir
        Bundler.bundleFile(masterSilkConfig, localSilkConfig)
        println("Cloning from site prototype : " + prototype.get + "...")
        Bundler.bundle(new File(siteProtoDir,  corePkgStr + fs + prototype.get + fs + "0.1.0"), userDir)
        println("Silk site prototype clone complete")
      } else println("No site prototype found with that id, please run silk sites")
    } else println("Please run silk update, there are no site prototypes on your system")
  }

  def componentClone(prototype: Option[String]) {
    if (silkHomeDir.exists) {
      if (new File(compDir,  corePkgStr + fs + prototype.getOrElse("nooo")).exists) {
        println("Cloning from component : " + prototype.get + "...")
        Bundler.bundle(new File(compDir, corePkgStr + fs + prototype.get + fs + "0.1.0"), userDir)
        println("Silk component clone complete")
      } else println("No component found with that id, please run silk components")
    } else println("Please run silk update, there are no components on your system")
  }

  // artifactName is either 'component' or 'site-prototype'
  def artifactInstall(artifactName: String, artifactBase: File) {
    val pkg = dnaConfig.getString(artifactName + ".package").replace(".", fs)
    val id = dnaConfig.getString(artifactName + ".id")
    val silkVersion = dnaConfig.getString(artifactName + ".silk-version")
    println("Installing %s : %s".format(artifactName, id))
    println("package is : " + pkg)
    println("silk version is : " + silkVersion)
    val artifactFile = new File(artifactBase + fs + pkg + fs + id + fs + silkVersion)
    if (artifactFile.exists) artifactFile.delete
    artifactFile.mkdirs
    Bundler.bundle(userDir, artifactFile)
    println("Silk " + artifactName + " install complete")
  }

  // @todo determine pipeline from Silk config
  def spin {
    ViewDrivenPipeline.process
    println("Silk spin complete")
  }
}

case class Config(var task: Option[String] = None, var prototype: Option[String] = None)
