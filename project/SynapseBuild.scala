import sbt._
import Keys._

object BuildSettings {
	
  val buildOrganization = "com.bheap.synapse"
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

  //val ax_version = "0.3-SNAPSHOT"
  val sc_version = "1.6.0-SNAPSHOT"
  val uf_version = "0.3.3"
  val st_version = "1.4.1"

  //val antiXML   = "com.codecommit"         %% "anti-xml"                % ax_version % "compile"
  val scalate   = "org.fusesource.scalate" % "scalate-core"             % sc_version % "compile"
  val uff       = "net.databinder"         %  "unfiltered-filter_2.8.1" % uf_version % "compile"
  val ufj       = "net.databinder"         %  "unfiltered-jetty_2.8.1"  % uf_version % "compile"
  val scalatest = "org.scalatest"          % "scalatest_2.9.0"          % st_version % "test"
}

object SynapseBuild extends Build {
  import Resolvers._
  import Dependencies._
  import BuildSettings._

  val scDeps = Seq(scalate)
  val ufDeps = Seq(uff, ufj)

  mainClass in (Compile, run) := Some("com.bheap.synapse.application.Server")

  lazy val synapse = Project(
    id = "synapse",
    base = file("."),
    settings = buildSettings,
    aggregate = Seq(kernel, tools, utils)
  )

  lazy val kernel = Project(
    id = "synapse-kernel",
    base = file("synapse-kernel"),
    dependencies = Seq(utils),
    settings = buildSettings ++ Seq(
      libraryDependencies ++= scDeps,
      libraryDependencies ++= ufDeps,
      resolvers := Seq(fSnapshots)
    )
  )

  lazy val tools = Project(
    id = "synapse-tools",
    base = file("synapse-tools"),
    dependencies = Seq(kernel),
    settings = buildSettings ++ Seq(
      libraryDependencies ++= scDeps,
      resolvers := Seq(fSnapshots)
    )
  )

  lazy val utils = Project(
    id = "synapse-utils",
    base = file("synapse-utils")
  )
}
