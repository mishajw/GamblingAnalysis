name := "GamblingAnalysis"

version := "1.0"

lazy val `gamblinganalysis` = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  jdbc, cache, ws, specs2 % Test,
  "org.jsoup"       %  "jsoup"             % "1.8.3",
  "org.xerial"      %  "sqlite-jdbc"       % "3.7.2",
  "org.scalikejdbc" %% "scalikejdbc"       % "2.3.5",
  "com.h2database"  %  "h2"                % "1.4.191",
  "ch.qos.logback"  %  "logback-classic"   % "1.1.3",
  "org.json4s"      %% "json4s-native"     % "3.3.0",
  "org.json4s"      %% "json4s-jackson"    % "3.3.0",
  "com.typesafe.akka" % "akka-actor_2.11"  % "2.4.2"
)

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )  

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
