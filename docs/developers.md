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


### Server development

The server (backend) part is written in Java using the Spring Boot framework.

Gradle is used to build the project. For local development you can run

```
./gradlew bootRun
```

in the root directory of the project. This will start the embedded Tomcat 
server on port 8080 and make your changes available. Make sure you have 
a proper configured `application.properties` file, either in 
`src/main/resources/application.properties` or somewhere else accessible.


### Client development

The client (frontend) part is a Single Page Application (SPA) with 
PWA features (Progressive Web Application) written in Javascript (Vue.js).

The sources are located in the `client` subdirectory.

For development you need [Node.js](https://nodejs.org/) and npm 
(Node Package Manager) installed on your system. Npm ships with Node.js, 
so you don't have to install it separately.

First of all run npm to install the dependencies:

```
cd client
npm install
```

After this you can run for development:

```
npm run dev
```

This will start a local web server on port 8081 with hot-reload, 
Lint-on-save, etc.

API requests are proxied to the Spring Boot tomcat server on port 8080, so make
sure this is also running.


### Build

The final build and executable jar file packaging is completely done using 
Gradle. You don't need to have Node.js and npm installed on your system. The 
gradle-node-plugin will take care of this.

To build the project run

```
./gradlew build
```

in the root directory.
