# domofon-tck

To make your life easier we created few compliance tests which could be used with your own implementation of Domofon API.
 
[![Build Status](https://travis-ci.org/blstream/domofon-tck.svg?branch=master)](https://travis-ci.org/blstream/domofon-tck)
[ ![Download](https://api.bintray.com/packages/lustefaniak/domofon/tck/images/download.svg) ](https://bintray.com/lustefaniak/domofon/tck/_latestVersion)
[![codecov.io](https://codecov.io/github/blstream/domofon-tck/coverage.svg?branch=master)](https://codecov.io/github/blstream/domofon-tck?branch=master)

Java 8 is required.

## Running TCK tests
 
 There are few possible ways to use `domofon-tck`, pick the one which suites you most:
 
### Standalone server in any language or framework using `tck-runner`

You can also use same TCK against server written in any language or framework.

Download `tck-runner` with all dependencies from [Github Releases](https://github.com/blstream/domofon-tck/releases) page.

```
unzip tck-runner-*.zip
./tck-runner-*/bin/tck-runner http://localhost:8080
```
 
### Use `akka-http` template project with `tck` included
 
 TODO
 
### Add `tck` tests to your `akka-http` based server
 
All tests use `ScalatestRouteTest` from akka-http testkit. To get started add SBT dependency:
 
  ```
  resolvers += Resolver.bintrayRepo("lustefaniak", "domofon")
  
  libraryDependencies += "com.blstream.domofon" %% "tck" %% "0.4.0" % "test"
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
You need to have Java 8 installed to use the mock. Or you can use [Docker image](#docker-images)

Download `akka-http-mock-server` with all dependencies from [Github Releases](https://github.com/blstream/domofon-tck/releases) page.
```
unzip akka-http-mock-server-*.zip
./akka-http-mock-server-*/bin/akka-http-mock-server
```

By default it will start mock server on http://0.0.0.0:8080/ so it will be publicly available, keep that in mind.  
  
You can use `--listen http://127.0.0.1:12345` option to override port on which mock should be running.

There might be more configuration options available in the future, you can check them using `--help` option.

## Docker images

Both `tck-runner` and `akka-http-mock-server` are available as docker images.

To run mock server
```
docker run -p 8080:8080 --rm lustefaniak/domofon-akka-http-mock-server:latest
```

Next you can run TCK suite using:

```
docker run --rm lustefaniak/domofon-tck-runner:latest http://localhost:8080
```

If you are using Docker for Mac or Docker for Windows, you might have to use `docker.local` instead of `localhost`.
