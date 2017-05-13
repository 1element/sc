## Development environment

If you want to develop new features or make bug fixes and need to compile the 
source code yourself, here are some notes on how to setup a local development 
environment.

Make sure you have the [Java 8 Development Kit](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
installed on your system.

To grab the source code of Surveillance Center, first clone the git repository:

```
git clone https://github.com/1element/sc.git
```

If you later want to contribute your code and make a pull request, 
you probably want to fork the repository first and clone your own fork.
There are some additional notes for this in the [contributing document](https://github.com/1element/sc/blob/master/CONTRIBUTING.md).

Checkout the `develop` branch as this is the branch where development happens.

```
git checkout develop
```

Gradle is used to build the project. Execute in the root directory 
of the project:

```
./gradlew build
```

To run the Spring Boot application execute:

```
./gradlew bootRun
```
