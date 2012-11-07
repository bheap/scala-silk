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

import java.io._
import java.net._
import java.nio._
import java.nio.channels._

import scopt._

import org.silkyweb.pipeline.ViewDrivenPipeline
import org.silkyweb.utils.{Archive, Bundler, Config => SilkConfig, Scout, Path}

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

    val tasks = "(update|sites|clone-site|install-site|components|clone-component|install-component|spin)"

    var config = Config()

    val parser = new OptionParser("silk") {
      arg("<task>", "task is a string property " + tasks, {t: String => config.task = Some(t)})
      argOpt("<prototype>", "a site or component prototype", {ps: String => config.prototype = Some(ps)})
      argOpt("<directory>", "a directory for cloning to occur in", {d: String => config.directory = Some(d)})
    }

    if (parser.parse(args)) {
      config.task.get match {
        case "sites" => artifactDisplay(config.prototype, "site-prototype", siteProtoDir)
        case "components" => artifactDisplay(config.prototype, "component", compDir)
        case "clone-site" => artifactClone(config.prototype, config.directory, "site-prototype", siteProtoDir)
        case "install-site" => artifactInstall("site-prototype", siteProtoDir)
        case "clone-component" => artifactClone(config.prototype, config.directory, "component", compDir)
        case "install-component" => artifactInstall("component", compDir)
        case "spin" => spin
        case "update" => update
        case _ => println("Sorry, not a valid action, please try " + tasks)
      }
    } else {
      println("Sorry, something went wrong, please try your command again...")
    }
  }

  /** Displays a list of artifacts for the given filter.
    *
    * @param filter Option[String] a path directory
    * @param artifactName String either 'site-prototype' or 'component'
    * @param artifactBase File a root directory */
  def artifactDisplay(filter: Option[String], artifactName: String, artifactBase: File) {
    val path = if (filter.isDefined) filter.get.replace(".", SilkConfig.fs) else "" 
    val base = new File(artifactBase.getPath, path)
    if (base.exists) {
      println("Installed " + artifactName + "s in " + base.getPath + " are:\n")
      Scout.getArtifacts(base, artifactName).foreach {
        item =>
          println(item.id + " : " + item.pkg + " : " + item.silkVersion)
          println("  " + item.desc)
      }
    } else println("Sorry, directory: %s does not exist.".format(base))
  }

  /** Clone an artifact.
    *
    * Currently either a Site Prototype or a Component.
    *
    * @param prototype Option[String] an id for a site-prototype or component
    * @param artifactName String either 'site-prototype' or 'component'
    * @param artifactBase File a root directory */
  def artifactClone(prototype: Option[String], directory: Option[String], artifactName: String, artifactBase: File) {
    if (silkHomeDir.exists) {
      val projectId = prototype getOrElse "nooo"
      val dir = directory getOrElse projectId
      var artifacts = Scout.getArtifactsById(artifactBase, artifactName, projectId)
      if (artifacts.size > 0) {
        val packages = artifacts.map(_.pkg).distinct
        if (packages.size > 1) {
          val selectedPackage = userSelected(packages, projectId, "package")
          artifacts = artifacts.filter(_.pkg == selectedPackage)
        }
        val selectedVersion = autoSelectedVersion(artifacts.map(_.silkVersion).distinct, projectId)
        artifacts = artifacts.filter(_.silkVersion == selectedVersion)
        if (artifactName.equals("site-prototype")) {
          val fname = new File(userDir + fs + dir + fs + "config")
          if (!fname.exists) fname.mkdirs
          Bundler.bundleFile(masterSilkConfig, new File(fname, "silk.conf"))
        }
        Bundler.bundle(artifacts.head.baseDir, new File(userDir, dir))
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
  // @todo refactor this into something more functional
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

  /** Determines silk version. Warns if artifact or user's silk version are out of date. 
    *
    * @param versions List[String] list of versions
    * @param projectId String display the projectId to user
    * @return String from the list of choices */
  def autoSelectedVersion(versions: List[String], projectId: String): String = {
    var version: String = versions.sortBy(s => s).last
    val runningSilkVersion = Info.version.split("-")(0) // Strip SNAPSHOT, ALPHA, BETA etc
    if (version < runningSilkVersion) {
      userCanAbortClone("WARNING: '%s' was built with an older version (%s), some compatibility changes may be required to spin.".format(projectId, version))
    } else if (version > runningSilkVersion) {
      userCanAbortClone("WARNING: '%s' was built with a newer version (%s), some compatibility changes may be required to spin. Please get the latest version from http://www.silkyweb.org.".format(projectId, version))
      if (versions.size > 1)
        version = userSelected(versions, projectId, "version")
    }
    version
  }

  /** Warns and allows user to abort the clone.
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
    val pkg = dnaConfig.getString(artifactName + ".package")
    val id = dnaConfig.getString(artifactName + ".id")
    val desc = dnaConfig.getString(artifactName + ".description")
    val silkVersion = dnaConfig.getString(artifactName + ".silk-version") 
    val artifactFile = new File(artifactBase + fs + pkg.replace(".", fs) + fs + id + fs + silkVersion)

    val runningSilkVersion = Info.version.split("-")(0) // Strip SNAPSHOT, ALPHA, BETA etc
    if (silkVersion != runningSilkVersion) {
      updateDnaConfigSetting(artifactName + ".silk-version", runningSilkVersion, true)
      artifactInstall(artifactName, artifactBase)
      sys.exit(0)
    }

    println("\nYou entered the following details;")
    println("  Package: %s".format(pkg))
    println("  ID: %s".format(id))
    println("  Description: %s\n".format(desc))    
    
    if (artifactFile.exists) {
     print("[WARNING] This will overwrite a local Silk site. ")
    }

    print("Do you wish to clone with these details(y/n): ")
    if (!readBoolean) {
      println("\nPlease amend the following details. Leave blank to remain the same.")
      changeSetting("Package",  artifactName + ".package", pkg, false)
      changeSetting("ID", artifactName + ".id", id, false)
      changeSetting("Description", artifactName + ".description", desc, true)
      artifactInstall(artifactName, artifactBase)
      sys.exit(0)
    }

    if (artifactFile.exists) artifactFile.delete
    artifactFile.mkdirs
    Bundler.bundle(userDir, artifactFile)
    println("Silk %s : %s install complete".format(artifactName, id))
  }

  /** Allows the user to change a setting.
    *
    * @param name String the name to display to the user
    * @param key String the setting key
    * @param value String value before change
    * @param addQuotes Boolean if value should be wrapped with quotes */  
  def changeSetting(name: String, key: String, value: String, addQuotes: Boolean) {
    var newValue: String = null
    print("%s [%s]: ".format(name, value)) 
    while (newValue == null) {
      newValue = try { readLine } catch { case _ => null }
    }
    if (newValue != value && !newValue.isEmpty) {
      updateDnaConfigSetting(key, newValue, addQuotes)
    }
  }

  /** Spin a silk site.
    *
    * Entails processing the configured pipeline. */
  // @todo determine pipeline from Silk config
  def spin {
    try {
      ViewDrivenPipeline.process
      println("Silk spin complete")
    } catch {
      case nex: NullPointerException =>
        println("Sorry, something has gone wrong...")
        println("Are you running 'silk spin' inside a valid Silk project ?")
    }
  }

  /** A platform independent update. */
  // @todo create the directories required
  def update {
    // First update silk.conf
    if (masterSilkConfig.exists) masterSilkConfig.delete else silkRepoDir.mkdirs
    downloadToLocation(new URL(updateUrlBase + "silk.conf"), new File(silkHomeDir, "silk.conf"))

    // Update site prototypes
    val spDir = new File(siteProtoStr + fs + corePkgStr)
    if (spDir.exists) Path.deleteAll(spDir)
    downloadToLocation(new URL(updateUrlBase + "site-prototype.zip"), new File(silkRepoDir, "site-prototype.zip"))
    Archive.extract(silkRepoStr + fs + "site-prototype.zip")

    // Update components
    val cpDir = new File(compStr + fs + corePkgStr)
    if (cpDir.exists) Path.deleteAll(cpDir)
    downloadToLocation(new URL(updateUrlBase + "component.zip"), new File(silkRepoDir, "component.zip"))
    Archive.extract(silkRepoStr + fs + "component.zip")

    println("Silk update complete.")
  }

  /** Downloads a given URL to a given location. */
  def downloadToLocation(url: URL, location: File) {
    val channel = Channels.newChannel(url.openStream)
    val fos = new FileOutputStream(location)
    fos.getChannel().transferFrom(channel, 0, 1 << 24)
  }
}

case class Config(var task: Option[String] = None, var prototype: Option[String] = None, var directory: Option[String] = None)
