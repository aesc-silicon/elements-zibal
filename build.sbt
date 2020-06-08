val spinalVersion = "1.4.0"

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "com.github.spinalhdl",
	  scalaVersion := "2.11.12",
      version      := "1.4.0"
    )),
    libraryDependencies ++= Seq(
//      "com.github.spinalhdl" % "spinalhdl-core_2.11" % "1.3.2",
//      "com.github.spinalhdl" % "spinalhdl-lib_2.11" % "1.3.2",
     
	 "com.github.spinalhdl" % "spinalhdl-core_2.11" % spinalVersion,
  	 "com.github.spinalhdl" % "spinalhdl-lib_2.11" % spinalVersion,
	 
	 "org.scalatest" % "scalatest_2.11" % "2.2.1",
     "org.yaml" % "snakeyaml" % "1.8",
	 
	 compilerPlugin("com.github.spinalhdl" % "spinalhdl-idsl-plugin_2.11" % spinalVersion)
    ),
    name := "Zibal",
    scalaSource in Compile := baseDirectory.value / "hardware" / "scala",
    scalaSource in Test    := baseDirectory.value / "test" / "scala"
  ).dependsOn(vexRiscv)

lazy val vexRiscv = RootProject(file("../elements-VexRiscv-dev/"))

fork := true
