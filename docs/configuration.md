## Configuration

You can configure Surveillance Center by editing the `application.properties` 
file and restart the application afterwards.

There is an example file [application-example.properties](https://github.com/1element/sc/blob/master/src/main/resources/application-example.properties) 
with annotations that shows all configuration options.

It's not necessary to change all default values, but there a few required ones.

Most features like push notifications, camera health check or off-site backup 
are disabled by default.

At least the following settings must be changed to reflect your setup:

```
###################
# Global settings #
###################
# Absolute path to the directory to save images.
sc.image.storage-dir=/home/surveillance/images/

# List of available camera ids (comma separated, don't use any special characters).
# Each camera id listed here must have it's own configuration key (sc.camera[id]), see below.
sc.cameras.available=front,backyard

###################
# Camera settings #
###################
# This is the main configuration part. Each camera you want to use must be listed in sc.cameras.available
# and configured here (camera id in brackets).

### Front door camera ###
sc.camera[front].name=Front door

# Camera host used for ping health check (see below). Only used when sc.healthcheck.enabled is set to true.
sc.camera[front].host=192.168.1.30

# Url used to display snapshots (live view).
sc.camera[front].url.snapshot=https://192.168.1.30/snapshot.cgi

# Url used to display live stream.
sc.camera[front].url.stream=https://192.168.1.30/videostream.cgi

# Ftp username for incoming images. This is used for camera identification and must be unique!
sc.camera[front].ftp.username=camera1

# Ftp password for incoming images.
sc.camera[front].ftp.password=password

# Incoming ftp directory. This is the place where new surveillance images for this camera will be put for a short
# period, until thumbnails are generated and they are moved to sc.image.storage-dir.
sc.camera[front].ftp.incoming-dir=/home/surveillance/ftp/camera1/

###############
# Data source #
###############
# Datasource url. If you want stick to the hsqldb, make sure the file path matches to your environment.
# If there is no existing database it will be created for you on application startup.
spring.datasource.url=jdbc:hsqldb:file:/home/surveillance/db/surveillance.db
```

After modifying the `application.properties` file don't forget to stop and 
restart the application to see your changes.
