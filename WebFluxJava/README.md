## WebFlux Spring Java Application

* Spring Boot 2
* Spring WebFlux
* MongoDB
* Maven
* Docker 

###### Start mongodb:

```
docker-compose up
```

or local version: 

```
C:\Program Files\MongoDB\Server\3.4\bin>mongod --dbpath C:\mongodb_data\ms18_data
```

###### test:
Endpoint per estrazione non bloccante. 
Per attivare il reactive-pattern e l'invio di eventi da server, 
nella url di request è necessario specificare l'header che indica di accettare 'text/event-stream':

```
curl -H "Accept: text/event-stream" "http://localhost:8081/api/cv/vociCV-nonBlocking"
```

Endpoint per chiamata da client (interno) per estrazione non bloccante. 

```
curl "http://localhost:8081/client/vociCV"
```
