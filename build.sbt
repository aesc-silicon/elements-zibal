val spinalVersion = "1.10.1"

lazy val root = (project in file("."))
  .settings(
    name := "Zibal",
    inThisBuild(
      List(
        organization := "com.github.spinalhdl",
        scalaVersion := "2.11.12",
        version := "2.0.0"
      )
    ),
    libraryDependencies ++= Seq(
      "com.github.spinalhdl" % "spinalhdl-core_2.11" % spinalVersion,
      "com.github.spinalhdl" % "spinalhdl-lib_2.11" % spinalVersion,
      compilerPlugin("com.github.spinalhdl" % "spinalhdl-idsl-plugin_2.11" % spinalVersion),
      "org.scalatest" %% "scalatest" % "3.2.5",
      "org.yaml" % "snakeyaml" % "1.8"
    ),
    Compile / scalaSource := baseDirectory.value / "hardware" / "scala",
    Test / scalaSource := baseDirectory.value / "test" / "scala"
  )
  .dependsOn(nafarr, vexRiscv, spinalCrypto)

lazy val nafarr = RootProject(file("../nafarr/"))
lazy val vexRiscv = RootProject(file("../vexriscv/"))
lazy val spinalCrypto = RootProject(file("../SpinalCrypto/"))

run / connectInput := true
fork := true
