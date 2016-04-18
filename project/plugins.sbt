resolvers += "sbt plugins" at "http://repo.scala-sbt.org/scalasbt/sbt-plugin-releases/"
resolvers += "Typesafe Repository" at "https://repo.typesafe.com/typesafe/releases/"

addSbtPlugin("io.spray" % "sbt-revolver" % "0.8.0")

addSbtPlugin("com.typesafe.sbt" % "sbt-git" % "0.8.5")

addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.5.0")

addSbtPlugin("org.scalariform" % "sbt-scalariform" % "1.6.0")

addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.3.5")

addSbtPlugin("me.lessis" % "bintray-sbt" % "0.3.0")

addSbtPlugin("io.gatling" % "gatling-sbt" % "2.1.7")

addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.1.0-RC1")