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

@SpringBootApplication
// @ComponentScan(basePackageClasses = {MyRestController.class, MyAuthService.class, EncryptionUtils.class})
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
            voceCVMongoNonBlockingRepo.deleteAll();

            // Inserimento vociCV
            VoceCV voceCV1 = new VoceCV("anagrafica", "nome", "Francesco");
            voceCVMongoNonBlockingRepo.save(voceCV1);
            VoceCV voceCV2 = new VoceCV("anagrafica", "cognome", "Imperato");
            voceCVMongoNonBlockingRepo.save(voceCV2);


        } catch(Exception e) {
            log.error("ERRORE in application run (MongoDB not started on host): " +e.getMessage());
        }
    }
}