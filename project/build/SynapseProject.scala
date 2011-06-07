import sbt._

class SynapseProject(info: ProjectInfo) extends DefaultProject(info) {

  override def mainClass = Some("com.bheap.synapse.application.Server")

  val sTSnapshots = "Scalatools Snapshots" at "http://scala-tools.org/repo-snapshots/"
  val fSnapshots  = "Fusesource Snapshots" at "http://repo.fusesource.com/nexus/content/repositories/snapshots/"

  // Versions
  lazy val ax_version = "0.3-SNAPSHOT"
  lazy val sc_version = "1.6.0-SNAPSHOT"
  lazy val uf_version = "0.3.3"
  lazy val st_version = "1.4.1"

  // Dependencies
  object Dependencies {
    lazy val antiXML   = "com.codecommit"         %% "anti-xml"                % ax_version % "compile"
    lazy val scalate   = "org.fusesource.scalate" % "scalate-core"             % sc_version % "compile"
    lazy val uff       = "net.databinder"         %  "unfiltered-filter_2.8.1" % uf_version % "compile"
    lazy val ufj       = "net.databinder"         %  "unfiltered-jetty_2.8.1"  % uf_version % "compile"
    lazy val scalatest = "org.scalatest"          % "scalatest_2.9.0"          % st_version % "test"
  }
  import Dependencies._

  lazy val synapse_kernel = project("synapse-kernel", "synapse-kernel", new SynapseKernelProject(_), synapse_utils)
  lazy val synapse_tools  = project("synapse-tools",  "synapse-tools",  new SynapseToolsProject(_),  synapse_utils)
  lazy val synapse_utils  = project("synapse-utils",  "synapse-utils",  new SynapseUtilsProject(_))

  class SynapseKernelProject(info: ProjectInfo) extends DefaultProject(info) {
    val antiXML   = Dependencies.antiXML
    val scalate   = Dependencies.scalate
    val uff       = Dependencies.uff
    val ufj       = Dependencies.ufj
    val scalatest = Dependencies.scalatest
  }

  class SynapseToolsProject(info: ProjectInfo) extends DefaultProject(info) {
    val antiXML = Dependencies.antiXML
    val scalate = Dependencies.scalate
  }

  class SynapseUtilsProject(info: ProjectInfo) extends DefaultProject(info)
}
