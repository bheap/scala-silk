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

package com.bheap.silk.utils

import java.io.File

import com.typesafe.config._

/** Leverage Typesafe Config.
  *
  * We load the config in our on way but essentially just leverage HOCON.
  *
  * As a side effect we also define some useful properties directly here.
  *
  * @author <a href="mailto:ross@bheap.co.uk">rossputin</a>
  * @since 1.0 */
object SilkConfig {

  val userHomeDirStr = System.getProperty("user.home")
  val userHomeDir = new File(userHomeDirStr)
  val silkHomeDir = new File(userHomeDir, ".silk")
  val userDirStr = System.getProperty("user.dir")
  val userDir = new File(userDirStr)

  val masterSilkConfig = new File(silkHomeDir, "silk.conf")
  val localSilkConfigDir = new File(userDir, ".silk")
  val localSilkConfig = new File(localSilkConfigDir, "silk.conf")
  val localDnaDir = new File(userDir, ".dna")
  val localDnaConfig = new File(localDnaDir, "dna.conf")

  val silkConfig = if (localSilkConfig.exists) ConfigFactory.parseFile(localSilkConfig) else ConfigFactory.parseFile(masterSilkConfig)
  val dnaConfig = ConfigFactory.parseFile(localDnaConfig)
}
