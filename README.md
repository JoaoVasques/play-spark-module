Play Spark Module [![Build Status](https://travis-ci.org/JoaoVasques/play-spark-module.svg?branch=master)](https://travis-ci.org/JoaoVasques/play-spark-module) [![Codacy Badge](https://api.codacy.com/project/badge/grade/d12a9692b867443cbdd3a69964b0034d)](https://www.codacy.com)
----------------------------

[![Join the chat at https://gitter.im/JoaoVasques/play-spark-module](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/JoaoVasques/play-spark-module?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

This is a plugin for Play 2.4, enabling support for [Apache Spark](http://spark.apache.org/). The goal is to simply Spark Context management and job submission.

Some considerations:

+ This project is under active development and the APIs might change in the future.
+ It wasn't tested on a production environment (yet!)
+ No remote spark job submissions tested (yet)

For contribution please check [CONTRIBUTING.md](https://github.com/JoaoVasques/play-spark-module/blob/master/CONTRIBUTING.md)

### Requirements

+ JRE 1.8

### Build manually

To build from source run

    sbt publish-local

To run all tests, first start mongodb locally (an actor persistence storage is being developed) and then run:

    sbt test

### API

The API is divided into two categories/groups:

+ Context API: for managing spark contexts and configuration
+ Job API: for job submission


```scala
trait PlaySparkApi {

  // Context API
  def saveSparkContext(conf: SparkConf)(implicit timeout: Timeout): Future[Try[String]]
  def startContext(contextId: String)(implicit timeout: Timeout): Future[Try[Unit]]
  def getContextsConfig()(implicit timeout: Timeout): Future[List[SparkConf]]
  def deleteContext(contextId: String)(implicit timeout: Timeout): Future[Try[Unit]]
  def stopContext()(implicit timeout: Timeout): Future[Try[Unit]]

  // Job API
  def startJob(job: SparkJob, contextId: String)(implicit timeout: Timeout): Future[SparkJobResult]
  def startSyncJob(job: SparkJob, contextId: String)(implicit timeout: Timeout): SparkJobResult
}

```

Before calling any method from the Job API, you must create a spark context. This methods creates a spark context using a configuration you chose. Remember that you only can have one Spark Context running per JVM. You can do so by calling:

```scala
def saveSparkContext(conf: SparkConf)(implicit timeout: Timeout): Future[Try[String]]
```

For more detailed API information please check the [Wiki](https://github.com/JoaoVasques/play-spark-module/wiki).

### Additional Documentation

To generate Scaladoc documentation (on folder doc/) just run:

```
  sbt doc
```

