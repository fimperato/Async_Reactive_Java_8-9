package it.imperato.test.webflux;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WebFluxClientJavaApplication implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(WebFluxClientJavaApplication.class);

    public static void main(String[] args){
        log.info("Spring Boot WebFluxClientJava Application started.");
        SpringApplication.run(WebFluxClientJavaApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        try {
            log.info("[WebFluxClientJavaApplication] run");

        } catch(Exception e) {
            log.error("ERRORE in application run" +e.getMessage());
        }
    }
}