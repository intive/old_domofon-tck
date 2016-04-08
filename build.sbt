import com.typesafe.sbt.SbtScalariform
import com.typesafe.sbt.SbtScalariform.ScalariformKeys
import scalariform.formatter.preferences._

cancelable in Global := true

val akkaVersion = "2.4.3"
val scalatestVersion = "3.0.0-M15"

lazy val commonSettings: Seq[sbt.Setting[_]] = SbtScalariform.defaultScalariformSettings ++ Seq(
  ivyScala := ivyScala.value map {
    _.copy(overrideScalaVersion = true)
  },
  updateOptions := updateOptions.value.withCachedResolution(true),
  ScalariformKeys.preferences := ScalariformKeys.preferences.value
    .setPreference(AlignSingleLineCaseStatements, true)
    .setPreference(SpacesAroundMultiImports, false)
    .setPreference(DoubleIndentClassDeclaration, true),
  git.useGitDescribe := true,
  organization := "com.blstream.domofon",
  scalaVersion := "2.11.7",
  crossScalaVersions := Seq("2.11.7"),
  licenses +=("MIT", url("http://opensource.org/licenses/MIT")),
  git.uncommittedSignifier := None,
  publishArtifact in Test := false,
  homepage := Some(url("https://github.com/blstream/domofon-tck")),
  description := "TCK tests for Domofon API",
  pomExtra :=
    <scm>
      <url>git@github.com:blstream/akka-viz.git</url>
      <connection>scm:git:git@github.com:blstream/domofon-tck.git</connection>
    </scm>
      <developers>
        <developer>
          <id>lustefaniak</id>
          <url>https://github.com/lustefaniak</url>
        </developer>
      </developers>, {
    import scala.xml.{Node => XmlNode, NodeSeq => XmlNodeSeq, _}
    import scala.xml.transform.{RewriteRule, RuleTransformer}

    def omitDep(e: Elem): XmlNodeSeq = {
      val organization = e.child.filter(_.label == "groupId").flatMap(_.text).mkString
      val artifact = e.child.filter(_.label == "artifactId").flatMap(_.text).mkString
      val version = e.child.filter(_.label == "version").flatMap(_.text).mkString
      Comment(s"dependency $organization#$artifact;$version has been omitted")
    }

    pomPostProcess := { (node: XmlNode) =>
      new RuleTransformer(new RewriteRule {
        override def transform(node: XmlNode): XmlNodeSeq = node match {
          case e: Elem if e.label == "dependency" && e.child.exists(child => child.label == "scope" && (child.text == "provided" || child.text == "test")) =>
            omitDep(e)
          //case e: Elem if e.label == "dependency" && e.child.exists(child => child.label == "groupId" && child.text == "com.typesafe.akka") =>
          //  omitDep(e)
          case _ => node
        }
      }).transform(node).head
    }
  }
)


lazy val domofon =
  (project in file("."))
    .disablePlugins(SbtScalariform)
    .enablePlugins(GitVersioning, RevolverPlugin)
    .settings(commonSettings)
    .settings(
      addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full),
      libraryDependencies += "com.wacai" %% "config-annotation" % "0.3.4" % "compile",
      libraryDependencies ++= Seq(
        "com.typesafe.akka" %% "akka-http-experimental" % akkaVersion,
        "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion,
        "com.typesafe.akka" %% "akka-http-testkit" % akkaVersion,
        "com.typesafe.akka" %% "akka-http-spray-json-experimental" % akkaVersion

      ),
      libraryDependencies += "org.scalatest" %% "scalatest" % scalatestVersion ,
      scalacOptions += "-Xmacro-settings:conf.output.dir=" + baseDirectory.value / "src/main/resources/"
    )

addCommandAlias("formatAll", ";scalariformFormat;test:scalariformFormat")