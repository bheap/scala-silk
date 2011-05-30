import sbt._

class SynapseProject(info: ProjectInfo) extends DefaultProject(info) {
  val antiXML = "com.codecommit" %% "anti-xml" % "0.3-SNAPSHOT"
}
