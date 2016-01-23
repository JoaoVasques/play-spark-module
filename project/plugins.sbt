// Comment to get more information during initialization
logLevel := Level.Warn

addSbtPlugin("com.typesafe.sbt" % "sbt-scalariform" % "1.3.0")

// The Typesafe repository
resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

// Bintray stuff

addSbtPlugin("me.lessis" % "bintray-sbt" % "0.3.0")

// Sonatype
addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "1.0")

