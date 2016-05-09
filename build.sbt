
organization := "com.abb"

name := "clpicklist"

version := "1.0"

scalaVersion in ThisBuild := "2.11.7"

scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature")

resolvers += Resolver.bintrayRepo("pathikrit", "maven")

resolvers += Resolver.sonatypeRepo("public")

scapegoatVersion := "1.1.0"

libraryDependencies ++= Seq(
  "org.scalikejdbc"            %% "scalikejdbc"               % "2.4.0",
  "org.scalikejdbc"            %% "scalikejdbc-interpolation" % "2.4.0",
  "org.scalikejdbc"            %% "scalikejdbc-config"        % "2.4.0",
  "org.scalikejdbc"            %% "scalikejdbc-jsr310"        % "2.4.0,
  "org.scalatest"              %% "scalatest"                 % "2.2.5"    % "test",
  "com.github.pathikrit"       %% "better-files"              % "2.15.0",
  "com.typesafe.scala-logging" %% "scala-logging"             % "3.1.0",
  "com.github.scopt"           %% "scopt"                     % "3.3.0",
  "org.apache.poi"             %  "poi"                       % "3.13",
  "org.apache.poi"             %  "poi-ooxml"                 % "3.13",
  "ch.qos.logback"             %  "logback-classic"           % "1.1.3",
  "org.xerial"                 %  "sqlite-jdbc"               % "3.8.11.2"
)

// The following is set-up for sbt-assembly (https://github.com/sbt/sbt-assembly)
assemblyJarName in assembly := "scalalikejdbc_example.jar"

publishTo := Some(Resolver.file("file",  new File(Path.userHome.absolutePath+"/.m2/repository")))

// The following are scoverage variables (https://github.com/scoverage/sbt-scoverage)
coverageEnabled := false

coverageMinimum := 80

coverageFailOnMinimum := false
