# Surveillance Center

[![Build Status](https://travis-ci.org/1element/sc.svg?branch=master)](https://travis-ci.org/1element/sc)
[![Release](https://img.shields.io/github/release/1element/sc.svg?maxAge=3600)](https://github.com/1element/sc/releases/latest)
[![License](https://img.shields.io/github/license/1element/sc.svg?maxAge=2592000)](https://github.com/1element/sc/blob/master/LICENSE.md)

Surveillance Center is an open source, self-hosted, web-based video surveillance software.

It accepts images using the built-in FTP server or via an external
MQTT broker and has a web-based, mobile optimized interface to browse
and manage surveillance snapshots.


## Features

* Written in Java (Spring Boot). Runs on Windows, Mac OS X and Linux (Ubuntu, Raspbian, etc.)
* Progressive Web Application (PWA)
* Live stream and snapshot view of cameras (built-in proxy)
* Built-in FTP server to receive new surveillance images
* Accepts new surveillance images via MQTT alternatively
* Thumbnail overview of images
* Ability to archive already seen images (acknowledge images)
* Auto removal of old archived images
* Configurable push notifications (via pushover.net)
* Optional image copy to remote server (SFTP, FTP)
* Camera health check
* Status RSS feed

Surveillance Center doesn't do any kind of motion detection.
You either have to use the built-in motion detection of your IP camera or
a third party software like [motion](https://github.com/Motion-Project/motion).


## Screenshots

[![screenshot1](https://raw.githubusercontent.com/1element/sc/master/docs/screenshots/screenshot1-thumbnail.png)](https://raw.githubusercontent.com/1element/sc/master/docs/screenshots/screenshot1.png)
[![screenshot2](https://raw.githubusercontent.com/1element/sc/master/docs/screenshots/screenshot2-thumbnail.png)](https://raw.githubusercontent.com/1element/sc/master/docs/screenshots/screenshot2.png)
[![screenshot3](https://raw.githubusercontent.com/1element/sc/master/docs/screenshots/screenshot3-thumbnail.png)](https://raw.githubusercontent.com/1element/sc/master/docs/screenshots/screenshot3.png)


## Installation

1. Make sure you have the Java Runtime Environment Version 8 (or higher) installed on
your system. If not you can [download it at Oracle](http://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html).

2. Download the latest release of Surveillance Center (jar file) from
[the releases page](https://github.com/1element/sc/releases).

3. Save a copy of [application-example.properties](https://raw.githubusercontent.com/1element/sc/master/src/main/resources/application-example.properties)
as `application.properties` in the same directory where the downloaded
jar file is located.

   For instance:

   ```
   wget https://raw.githubusercontent.com/1element/sc/master/src/main/resources/application-example.properties -O application.properties
   ```

4. Edit the `application.properties` file. This is the main configuration file
where your credentials, IP cameras, push notifications, etc. are configured.

   Most settings can be left at the default value. But there are a few required
ones, see the [configuration document](https://github.com/1element/sc/blob/master/docs/configuration.md)
for details.

5. Run the following command in a terminal to start the embedded Apache Tomcat
application server:

   ```
   java -jar surveillancecenter-<VERSION>.jar
   ```

   Once you see the output line `Started SurveillanceCenterApplication in X.X seconds`
you should be able to visit [http://localhost:8080/sc/](http://localhost:8080/sc/)
in your browser.


## Further documentation

[configuration.md](https://github.com/1element/sc/blob/master/docs/configuration.md):
Some notes on how to configure the application (`application.properties`).

[integration.md](https://github.com/1element/sc/blob/master/docs/integration.md):
Useful notes if you want to use Surveillance Center with third party software
like Nginx or Motion.

[developers.md](https://github.com/1element/sc/blob/master/docs/developers.md):
For developers only. How to setup a local development environment and build
the project from the source code.


## Contributing

Contributions are welcome. Please read [CONTRIBUTING.md](https://github.com/1element/sc/blob/master/CONTRIBUTING.md)
for details.


## License

This project is licensed under the terms of the GNU Affero General Public License
as published by the Free Software Foundation, either version 3 of the License,
or (at your option) any later version.

For more information, see [LICENSE.md](https://github.com/1element/sc/blob/master/LICENSE.md).
