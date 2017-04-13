name := "scalaplot"

organization := "org.sameersingh.scalaplot"

version := "0.2-SNAPSHOT"

scalaVersion := "2.12.1"

crossScalaVersions := Seq("2.11.8", "2.12.1")

libraryDependencies += "jfree" % "jfreechart" % "1.0.13"
libraryDependencies += "com.itextpdf" % "itextpdf" % "5.1.2"
libraryDependencies += "com.itextpdf.tool" % "xmlworker" % "1.1.0"

libraryDependencies += "junit" % "junit" % "4.10" % Test
libraryDependencies += "com.novocode" % "junit-interface" % "0.11" % Test

testOptions in Test := Seq(Tests.Argument(TestFrameworks.JUnit, "-a"))