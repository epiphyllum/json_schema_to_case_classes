scalaVersion := "2.11.8"

val circeVersion = "0.5.1"

libraryDependencies ++= Seq(
  "com.chuusai" %% "shapeless"  % "2.3.2",
  "io.circe" %% "circe-core"    % circeVersion,
  "io.circe" %% "circe-generic" % circeVersion,
  "io.circe" %% "circe-parser"  % circeVersion
)