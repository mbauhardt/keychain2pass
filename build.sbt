name := "keychain2pass"

version := "0.1.2"

scalaVersion := "2.12.2"

javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-Xlint")

scalacOptions += "-target:jvm-1.8"

oneJarSettings

libraryDependencies += "org.scalactic" %% "scalactic" % "3.0.1"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.1" % "test"

mappings in(Compile, packageBin) += {
  (baseDirectory.value / "LICENSE") -> "LICENSE"
}
