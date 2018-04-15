package it.imperato.test.webflux.controller;

import it.imperato.test.webflux.client.WebClientVoceCV;
import it.imperato.test.webflux.model.VoceCV;
import it.imperato.test.webflux.repository.VoceCVMongoNonBlockingRepo;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping("/client")
public class InternalWebClientController {

    private static final Logger log = Logger.getLogger(InternalWebClientController.class.getName());

    @Autowired
    WebClientVoceCV webClient;

    @GetMapping("/vociCV")
    public String getVoceCVFlux() {
        log.info("## Get All Voci CV.");
        try {
            webClient.getAll()
                .subscribe(InternalWebClientController::elaborate);

            Thread.sleep(1000);
        } catch (Exception e) {
            log.log(Level.SEVERE,"Error: "+e.getMessage(),e);
        }
        return "OK";
    }

    private static void elaborate(VoceCV voceCV) {
        log.info("received "+voceCV + " at: "+new java.util.Date());

        // some task
        log.info("Task on: "+voceCV);
    }

}
