lazy val core = (project in file("core")).settings(
  organization := "tf.bug",
  name := "umi-core",
  version := "0.1.0",
  scalaVersion := "2.13.6",
  libraryDependencies ++= Seq(
    "org.typelevel" %% "cats-core" % "2.6.1",
    "org.typelevel" %% "cats-effect" % "3.2.0",
    "co.fs2" %% "fs2-core" % "3.0.6",
    "org.scalameta" %% "munit" % "0.7.27" % Test,
    "org.typelevel" %% "munit-cats-effect-3" % "1.0.5" % Test,
  ),
  addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1"),
)
