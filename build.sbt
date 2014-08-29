name := "value-explore"

scalaVersion in ThisBuild := "2.11.2"

scalacOptions ++= Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-feature",
  "-language:existentials",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-unchecked",
  "-Xfatal-warnings",
  "-Xlint",
  "-Yno-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Ywarn-value-discard",
  "-Xfuture"
)

resolvers += "Typesafe Repo" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies in ThisBuild ++= Seq(
  "org.specs2"         %% "specs2"        % "2.4.1" % "test",
  "org.scalaz"         %% "scalaz-core"   % "7.1.0",
  "org.scala-lang"     %  "scala-reflect" % scalaVersion.value,
  "com.typesafe.slick" %% "slick"         % "2.1.0",
  "org.squeryl"        %% "squeryl"       % "0.9.6-RC3",
  "org.postgresql"     %  "postgresql"    % "9.3-1101-jdbc41",
  "com.chuusai"        %% "shapeless"     % "2.0.0",
  "org.slf4j"          %  "slf4j-nop"     % "1.6.4",
  "org.scalaz"         %% "scalaz-core"   % "7.1.0"
)
