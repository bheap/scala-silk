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

import java.io.File

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

  // Core directory structures
  val compStr = silkRepoStr + fs + "component"
  val compDir = new File(compStr)
  val siteProtoStr = silkRepoStr + fs + "site-prototype"
  val siteProtoDir = new File(siteProtoStr)
  val templateStr = silkRepoStr + fs + "template"

  // Core package
  val corePkgStr = "org" + fs + "silkyweb"

  // User current Dir related
//  val userDirStr = System.getProperty("user.dir")
//  val userDir = new File(userDirStr)

  // Master config file
  val masterSilkConfig = new File(silkHomeDir, "silk.conf")

  // Local config files
//  val localSilkConfigDir = new File(userDir, "config")
  val localSilkConfig = new File(localSilkConfigDir, "silk.conf")
  val localDnaConfig = new File(localSilkConfigDir, "dna.conf")

  // There may be a local Silk conf which can override master Silk conf
  val silkConfig = if (localSilkConfig.exists) ConfigFactory.parseFile(localSilkConfig) else ConfigFactory.parseFile(masterSilkConfig)

  // There is always a dna.conf
  val dnaConfig = ConfigFactory.parseFile(localDnaConfig)
}
