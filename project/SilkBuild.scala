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

import sbt._
import Keys._
import sbtassembly.Plugin._
import AssemblyKeys._

object BuildSettings {
	
  val buildOrganization = "org.silkyweb.silk"
  val buildVersion      = "0.1.0-SNAPSHOT"
  val buildScalaVersion = "2.9.1"

  val buildSettings = Defaults.defaultSettings ++ Seq (
    organization := buildOrganization,
    version      := buildVersion,
    scalaVersion := buildScalaVersion,
    shellPrompt  := ShellPrompt.buildShellPrompt
  )
}

// Shell prompt which show the current project, 
// git branch and build version
object ShellPrompt {
	
  object devnull extends ProcessLogger {
    def info (s: => String) {}
    def error (s: => String) { }
    def buffer[T] (f: => T): T = f
  }

  def currBranch = (
    ("git status -sb" lines_! devnull headOption)
      getOrElse "-" stripPrefix "## "
  )

  val buildShellPrompt = { 
    (state: State) => {
      val currProject = Project.extract (state).currentProject.id
      "%s:%s:%s> ".format (
        currProject, currBranch, BuildSettings.buildVersion
      )
    }
  }
}

object Resolvers {

  val scalaToolsReleases = "Scalatools Releases" at "http://scala-tools.org/repo-releases"
  val typesafeReleases = "Typesafe Releases"    at "http://repo.typesafe.com/typesafe/releases"
  val fSnapshots       = "Fusesource Snapshots" at "http://repo.fusesource.com/nexus/content/repositories/snapshots/"
}

object Dependencies {

  val uf_version = "0.3.3"

  val antiXML    = "com.codecommit"         %% "anti-xml"                % "0.3"            % "compile"
  val config     = "com.typesafe.config"    % "config"                   % "0.2.0"          % "compile"
  val scalate    = "org.fusesource.scalate" % "scalate-core"             % "1.6.0-SNAPSHOT" % "compile"
  val scopt      = "com.github.scopt"       % "scopt_2.9.1"              % "1.1.2"          % "compile"
  val slf4j      = "org.slf4j"              % "slf4j-simple"             % "1.6.2"          % "compile"
  val scalaUtils = "com.bheap"              %% "scala-utils"             % "0.1.0-SNAPSHOT" % "compile"

  val scalatest  = "org.scalatest"          % "scalatest_2.9.0"          % "1.4.1"          % "test"
}

object SilkBuild extends Build {
  import Resolvers._
  import Dependencies._
  import BuildSettings._

  val anDeps = Seq(antiXML)
  val cfDeps = Seq(config)
  val scDeps = Seq(scalate)
  val slDeps = Seq(slf4j)
  val soDeps = Seq(scopt)
  val suDeps = Seq(scalaUtils)

  //jarName in Assembly := "silk.jar"

  lazy val silk = Project(
    id = "silk",
    base = file("."),
    settings = buildSettings,
    aggregate = Seq(kernel, utils, cli)
  ) settings(assemblySettings: _*)

  lazy val kernel = Project(
    id = "silk-kernel",
    base = file("silk-kernel"),
    dependencies = Seq(utils),
    settings = buildSettings ++ Seq(
      libraryDependencies ++= scDeps,
      libraryDependencies ++= slDeps,
      libraryDependencies ++= suDeps,
      resolvers := Seq(fSnapshots)
    )
  ) settings(assemblySettings: _*)

  lazy val utils = Project(
    id = "silk-utils",
    base = file("silk-utils"),
    settings = buildSettings ++ Seq(
      libraryDependencies ++= anDeps,
      libraryDependencies ++= cfDeps,
      resolvers := Seq(typesafeReleases)
    )
  ) settings(assemblySettings: _*)

  lazy val cli = Project(
    id = "silk-cli",
    base = file("silk-cli"),
    dependencies = Seq(kernel),
    settings = buildSettings ++ Seq(
      libraryDependencies ++= soDeps,
      resolvers := Seq(fSnapshots),
      mainClass in (Compile, packageBin) := Some("org.silkyweb.interface.Silk"),
      sourceGenerators in Compile <+= (sourceManaged in Compile, version, name) map { (d, v, n) =>
				val file = d / "info.scala"
				IO.write(file, """package org.silkyweb.interface
				  |object Info {
				  |  val version = "%s"
				  |  val name = "%s"
				  |}
				  |""".stripMargin.format(v, n))
				Seq(file)
      }
    )
  ) settings(assemblySettings: _*)
}
