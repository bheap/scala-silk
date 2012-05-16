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

package org.silkyweb.utils

import java.io.{File, FileInputStream, FileOutputStream}
import java.util.Properties

import com.typesafe.config._

/** Leverage Typesafe Config.
  *
  * We load the config in our own way but essentially just leverage HOCON.
  *
  * As a side effect we also define some useful properties directly here.
  *
  * @author <a href="mailto:ross@bheap.co.uk">rossputin</a>
  * @author <a href="mailto:nick.featch@gmail.com">schnick</a>
  * @since 1.0 */
object Config {

  // Our platform specific separator
  val fs = File.separator
  val fsCharArr = fs.toCharArray
  val fsChar = fsCharArr(0)

  // Home Dir related
  val userHomeDirStr = System.getProperty("user.home")
  val userHomeDir = new File(userHomeDirStr)
  val silkHomeStr = userHomeDirStr + fs + ".silk"
  val silkHomeDir = new File(silkHomeStr)

  val silkRepoStr = silkHomeStr + fs + "repositories"
  val silkRepoDir = new File(silkRepoStr)

  // Core directory structures
  val compStr = silkRepoStr + fs + "component"
  val compDir = new File(compStr)
  val siteProtoStr = silkRepoStr + fs + "site-prototype"
  val siteProtoDir = new File(siteProtoStr)
  val templateStr = silkRepoStr + fs + "template"

  // Core package
  val corePkgStr = "org" + fs + "silkyweb"

  // User current Dir related
  val userDirStr = System.getProperty("user.dir")
  val userDir = new File(userDirStr)

  // Master config file
  val masterSilkConfig = new File(silkHomeDir, "silk.conf")

  // Local config files
  val localSilkConfigDir = new File(userDir, "config")
  val localSilkConfig = new File(localSilkConfigDir, "silk.conf")
  val localDnaConfig = new File(localSilkConfigDir, "dna.conf")

  // Update URL related
  // @todo calculate version based on project version, dropping anyhting beyond patch version
  val updateUrlBase = "http://www.silkyweb.org/resource/downloads/silk/updates/0.1.0/"

  // There may be a local Silk conf which can override master Silk conf
  val silkConfig = if (localSilkConfig.exists) parse(localSilkConfig) else parse(masterSilkConfig)

  // There is always a dna.conf
  var dnaConfig = parse(localDnaConfig)

  /** Helper method to parse the given config file.
    *
    * @param configFile File the config file 
    * @return Config the configurations */  
  def parse(configFile: File) = ConfigFactory.parseFile(configFile)

  /** Update an artifacts local DNA config setting.
    *
    * @param setting String name of the setting to change 
    * @param newValue String the new value 
    * @param addQuotes Boolean if value should be wrapped with quotes */  
  def updateDnaConfigSetting(setting: String, newValue: String, addQuotes: Boolean) {
    val prop = new Properties
    val in = new FileInputStream(localDnaConfig)
    prop.load(in)
    prop.setProperty(setting, if (addQuotes) "\"%s\"".format(newValue) else newValue)
    val out = new FileOutputStream(localDnaConfig)
    prop.store(out, null)
    in.close
    out.close
    //Reload Dna Config
    dnaConfig = parse(localDnaConfig) 
  }
}
