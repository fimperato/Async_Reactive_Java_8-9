## WebFlux Client Spring Java Application

* Spring Boot 2
* Spring WebFlux (Client)
* Maven

###### Start mongodb:

```
docker-compose up
```

or local version: 

```
C:\Program Files\MongoDB\Server\3.4\bin>mongod --dbpath C:\mongodb_data\ms18_data
```

###### test:
Endpoint per chiamata da client per estrazione non bloccante. 

```
curl "http://localhost:8082/client/vociCV"
```
