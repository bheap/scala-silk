import sbt._
import Keys._
import sbtassembly.Plugin._
import AssemblyKeys._

object BuildSettings {
	
  val buildOrganization = "com.bheap.silk"
  val buildVersion      = "0.1-SNAPSHOT"
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
	
  val fSnapshots  = "Fusesource Snapshots" at "http://repo.fusesource.com/nexus/content/repositories/snapshots/"
}

object Dependencies {

  val sc_version = "1.6.0-SNAPSHOT"
  val so_version = "1.1.2"
  val uf_version = "0.3.3"
  val st_version = "1.4.1"

  //val scalate   = "org.fusesource.scalate" % "scalate-core"             % sc_version % "compile"
  val scopt     = "com.github.scopt"       % "scopt_2.9.1"              % so_version % "compile"
  val uff       = "net.databinder"         %  "unfiltered-filter_2.8.1" % uf_version % "compile"
  val ufj       = "net.databinder"         %  "unfiltered-jetty_2.8.1"  % uf_version % "compile"
  val scalatest = "org.scalatest"          % "scalatest_2.9.0"          % st_version % "test"
}

object SilkBuild extends Build {
  import Resolvers._
  import Dependencies._
  import BuildSettings._

  //val scDeps = Seq(scalate)
  val soDeps = Seq(scopt)
  val ufDeps = Seq(uff, ufj)

  //jarName in Assembly := "silk.jar"

  lazy val silk = Project(
    id = "silk",
    base = file("."),
    settings = buildSettings,
    aggregate = Seq(kernel, tools, utils, cli)
  ) settings(assemblySettings: _*)

  lazy val kernel = Project(
    id = "silk-kernel",
    base = file("silk-kernel"),
    dependencies = Seq(utils),
    settings = buildSettings ++ Seq(
      libraryDependencies ++= ufDeps,
      resolvers := Seq(fSnapshots)
    )
  ) settings(assemblySettings: _*)

  lazy val tools = Project(
    id = "silk-tools",
    base = file("silk-tools"),
    dependencies = Seq(kernel),
    settings = buildSettings ++ Seq(
      //libraryDependencies ++= scDeps,
      resolvers := Seq(fSnapshots)
    )
  ) settings(assemblySettings: _*)

  lazy val utils = Project(
    id = "silk-utils",
    base = file("silk-utils")
  ) settings(assemblySettings: _*)

  lazy val cli = Project(
    id = "silk-cli",
    base = file("silk-cli"),
    dependencies = Seq(tools),
    settings = buildSettings ++ Seq(
      libraryDependencies ++= soDeps,
      resolvers := Seq(fSnapshots),
      mainClass in (Compile, packageBin) := Some("com.bheap.silk.interface.Silk")
    )
  ) settings(assemblySettings: _*)
}
