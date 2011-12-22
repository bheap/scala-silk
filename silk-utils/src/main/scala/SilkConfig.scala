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
