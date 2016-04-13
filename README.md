# domofon-tck

To make your life easier we created few compliance tests which could be used with your own implementation of Domofon API.
 
[![Build Status](https://travis-ci.org/blstream/domofon-tck.svg?branch=master)](https://travis-ci.org/blstream/domofon-tck)
[ ![Download](https://api.bintray.com/packages/lustefaniak/domofon/tck/images/download.svg) ](https://bintray.com/lustefaniak/domofon/tck/_latestVersion)
[![codecov.io](https://codecov.io/github/blstream/domofon-tck/coverage.svg?branch=master)](https://codecov.io/github/blstream/domofon-tck?branch=master)


## Getting Started
 
 There are few possible ways to use `domofon-tck`, pick the one which suites you most:
 
### Standalone server in any language or framework using `tck-runner`

You can also use same TCK against server written in any language.
 
Simplest way to get started is downloading all dependencies using `coursier`.
 
First, install coursier using project documentation: https://github.com/alexarchambault/coursier#command-line

When coursier is installed, you can execute `tck-runner` using:

  ```
  coursier launch -r https://dl.bintray.com/lustefaniak/domofon/ "com.blstream.domofon:tck-runner_2.11:0.2.0" http://localhost:8080/
  ```

**TIP**: Usually it is best to use latest version of the TCK: [ ![Download](https://api.bintray.com/packages/lustefaniak/domofon/tck/images/download.svg) ](https://bintray.com/lustefaniak/domofon/tck/_latestVersion) 
 
### Use `akka-http` template project
 
 TODO
 
### Add `tck` tests to your `akka-http` based server
 
All tests use `ScalatestRouteTest` from akka-http testkit. To get started add SBT dependency:
 
  ```
  resolvers += Resolver.bintrayRepo("lustefaniak", "domofon")
  
  libraryDependencies += "com.blstream.domofon" %% "tck" %% "0.2.0" % "test"
  ```
  
**TIP**: Usually it is best to use latest version of the TCK: [ ![Download](https://api.bintray.com/packages/lustefaniak/domofon/tck/images/download.svg) ](https://bintray.com/lustefaniak/domofon/tck/_latestVersion)
 
Next, create new test:
 
   ```
   import domofon.tck.DomofonTck
 
   class AkkaHttpRouteTest extends DomofonTck {
     def domofonRoute: Route = ???
   }
   ```
 
After that all test cases will be executed against your implementation


## Mock server

Simplest way to get started is downloading all dependencies using `coursier`.
 
First, install coursier using project documentation: https://github.com/alexarchambault/coursier#command-line

When coursier is installed, you can launch `akka-http-mock-server` using:

  ```
  coursier launch -r https://dl.bintray.com/lustefaniak/domofon/ "com.blstream.domofon:akka-http-mock-server_2.11:0.2.0"
  ```

By default it will start mock server on http://0.0.0.0:8080/ so it will be publicly available, keep that in mind.  
  
You can use `--listen http://127.0.0.1:12345` option to override port on which mock should be running.

There might be more configuration options available in the future, you can check them using `--help` option.


**TIP**: Usually it is best to use latest version of the TCK: [ ![Download](https://api.bintray.com/packages/lustefaniak/domofon/tck/images/download.svg) ](https://bintray.com/lustefaniak/domofon/tck/_latestVersion)
