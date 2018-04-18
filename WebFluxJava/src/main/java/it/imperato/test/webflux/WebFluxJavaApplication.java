package it.imperato.test.webflux;

import it.imperato.test.webflux.model.VoceCV;
import it.imperato.test.webflux.repository.VoceCVMongoNonBlockingRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootApplication
// @ComponentScan(basePackageClasses = {CorsFilter.class, classes in it.imperato.test.webflux..})
public class WebFluxJavaApplication implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(WebFluxJavaApplication.class);

    @Autowired
    VoceCVMongoNonBlockingRepo voceCVMongoNonBlockingRepo;

    public static void main(String[] args){
        log.info("Spring Boot WebFluxJava Application started.");
        SpringApplication.run(WebFluxJavaApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        try {
            Flux<VoceCV> vociCVFlux = voceCVMongoNonBlockingRepo.findAll();
            log.info(vociCVFlux != null ? vociCVFlux.toString() : "N.D.");

            // Save initial voci cv
            // Rimuovo tutte le voci precedenti (dati di test)
            voceCVMongoNonBlockingRepo.deleteAll().block();

            VoceCV voceCV1 = new VoceCV("anagrafica", "nome", "Francesco");
            VoceCV voceCV2 = new VoceCV("anagrafica", "cognome", "Imperato");

            // Inserimento vociCV mvc-style:
            //voceCVMongoNonBlockingRepo.save(voceCV1);
            //voceCVMongoNonBlockingRepo.save(voceCV2);

            List<VoceCV> voceCVList = Stream
                    .of(voceCV1, voceCV2)
                    .collect(Collectors.toList());

            Flux<VoceCV> voceCVFlux = Flux.fromIterable(voceCVList);
            voceCVFlux
                    .map(v -> voceCVMongoNonBlockingRepo.save(v))
                    .subscribe(m -> log.info("Caricato la voce per CV: "+m.block())); // subscribe necessario per fare partire il job di save su mongodb
            log.info("MongoDB repository contiene ora: "+voceCVMongoNonBlockingRepo.count().block()+" voci-CV.");

        } catch(Exception e) {
            log.error("ERRORE in application run (MongoDB not started on host): " +e.getMessage());
        }
    }
}