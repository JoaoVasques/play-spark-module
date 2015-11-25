// Comment to get more information during initialization
logLevel := Level.Warn

addSbtPlugin("com.typesafe.sbt" % "sbt-scalariform" % "1.3.0")

addSbtPlugin("com.jsuereth" % "sbt-pgp" % "1.0.0")

// The Typesafe repository
resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

// Add required sbt plugins below
