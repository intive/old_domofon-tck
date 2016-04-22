import com.typesafe.sbt.SbtScalariform
import com.typesafe.sbt.SbtScalariform.ScalariformKeys
import scalariform.formatter.preferences._

cancelable in Global := true

val akkaVersion = "2.4.4"
val scalatestVersion = "3.0.0-M15"
val scalacheckVersion = "1.13.0"

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
  description := "TCK tests for Domofon API and mock server, implementation using Akka HTTP module",
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
  },
  bintrayCredentialsFile := file(".bintray_credentials"),
  bintrayVcsUrl := Some("https://github.com/blstream/domofon-tck.git"),
  bintrayRepository := "domofon"
) ++ Seq(
  dockerBaseImage := "anapsix/alpine-java:jre8",
  dockerUpdateLatest := true,
  maintainer in Docker := "Lukasz Stefaniak <lustefaniak@gmail.com>",
  dockerRepository := Some("lustefaniak")
)

lazy val `tck` =
  (project in file("tck"))
    .disablePlugins(SbtScalariform)
    .enablePlugins(GitVersioning)
    .settings(commonSettings)
    .settings(
      libraryDependencies ++= Seq(
        "com.typesafe.akka" %% "akka-http-experimental" % akkaVersion,
        "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion,
        "com.typesafe.akka" %% "akka-http-testkit" % akkaVersion,
        "com.typesafe.akka" %% "akka-http-spray-json-experimental" % akkaVersion,
        "de.heikoseeberger" %% "akka-sse" % "1.7.2"
      ),
      libraryDependencies ++= Seq(
        "org.scalatest" %% "scalatest" % scalatestVersion,
        "org.scalacheck" %% "scalacheck" % scalacheckVersion
      )
    )

lazy val `tck-runner` =
  (project in file("tck-runner"))
    .disablePlugins(SbtScalariform)
    .enablePlugins(GitVersioning, RevolverPlugin, JavaAppPackaging)
    .settings(commonSettings)
    .settings(
      packageName in Docker := "domofon-tck-runner"
    )
    .dependsOn(`tck`)

lazy val `akka-http-mock` =
  (project in file("akka-http-mock"))
    .disablePlugins(SbtScalariform)
    .enablePlugins(GitVersioning)
    .settings(commonSettings)
    .settings(
      resolvers += Resolver.bintrayRepo("hmrc", "releases"),
        libraryDependencies ++= Seq(
        "com.typesafe.akka" %% "akka-http-experimental" % akkaVersion,
        "com.typesafe.akka" %% "akka-http-spray-json-experimental" % akkaVersion,
        "de.heikoseeberger" %% "akka-sse" % "1.7.2",
        "org.typelevel"     %% "cats" % "0.4.1",
        "uk.gov.hmrc"       %% "emailaddress" % "1.1.0"
        ),
        libraryDependencies ++= Seq(
        "org.scalatest" %% "scalatest" % scalatestVersion % "test"
      )
    )
    .dependsOn(`tck` % "test")

lazy val `akka-http-mock-server` =
  (project in file("akka-http-mock-server"))
    .disablePlugins(SbtScalariform)
    .enablePlugins(GitVersioning, RevolverPlugin, JavaAppPackaging)
    .settings(commonSettings)
    .settings(
      libraryDependencies += "com.github.scopt" %% "scopt" % "3.4.0",
      libraryDependencies += "ch.megard" %% "akka-http-cors" % "0.1.1"
    )
    .settings(
      dockerExposedPorts := List(8080),
      packageName in Docker := "domofon-akka-http-mock-server",
      dockerCmd := Seq("--listen", "http://0.0.0.0:8080")
    )
    .dependsOn(`akka-http-mock`)

lazy val performance =
  (project in file("perf"))
    .enablePlugins(GatlingPlugin)
    .settings(commonSettings)
    .settings(
      libraryDependencies ++= Seq(
        "com.typesafe.akka" %% "akka-http-spray-json-experimental" % akkaVersion,
        "io.gatling.highcharts" % "gatling-charts-highcharts" % "2.1.7" % "test",
        "io.gatling" % "gatling-test-framework" % "2.1.7" % "test",
        "org.scalacheck" %% "scalacheck" % "1.13.0" % "test"
      )
    )
    .dependsOn(`akka-http-mock`)

lazy val root = (project in file("."))
  .settings(commonSettings)
  .enablePlugins(GitVersioning)
  .settings(
    publish := {},
    run := run in `akka-http-mock-server`
  ).aggregate(`tck`, `tck-runner`, `akka-http-mock`, `akka-http-mock-server`)


addCommandAlias("formatAll", ";scalariformFormat;test:scalariformFormat")
addCommandAlias("compileAll", ";compile;performance/test:compile")
