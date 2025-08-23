# Music App

### Short description

This is a microservices-based application developed as a part of **Introduction to Microservices** course.

The Resource Service implements CRUD operations for processing MP3 files. When uploading an MP3 file, the service:

* Stores the MP3 file in the database
* Extracts the MP3 file tags (metadata) using Apache Tika library
* Invokes the Song Service to save the MP3 file tags (metadata)

The Song Service implements CRUD operations for managing song metadata records. The service uses the Resource ID to uniquely identify each metadata record, establishing a direct one-to-one relationship between resources and their metadata.

### Guides

Manually install the domain module before running the services: `mvn clean install -DskipTests` 

In order to launch the app locally, run `docker-compose up -d` command in the root folder.

For API testing, update the default base paths:
Change / in Postman to:

* /resource-service/api/v1

* /song-service/api/v1



