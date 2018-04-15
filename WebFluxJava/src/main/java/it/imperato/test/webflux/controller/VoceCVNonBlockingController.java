package it.imperato.test.webflux.controller;

import it.imperato.test.webflux.model.VoceCV;
import it.imperato.test.webflux.repository.VoceCVMongoNonBlockingRepo;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.time.Duration;

@RestController
@RequestMapping("/api/cv")
public class VoceCVNonBlockingController {

    private static final int DELAY_PER_ITEM_MS = 3000;

    private VoceCVMongoNonBlockingRepo voceCVMongoReactiveRepository;

    public VoceCVNonBlockingController(final VoceCVMongoNonBlockingRepo voceCVMongoNonBlockingRepo) {
        this.voceCVMongoReactiveRepository = voceCVMongoNonBlockingRepo;
    }

    @GetMapping(path = "/vociCV-nonBlocking", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ResponseBody
    public Flux<VoceCV> getVoceCVFlux() {
        // to use a shorter version of the Flux -> take(n) at the end of the statement:
        return voceCVMongoReactiveRepository.findAll().delayElements(Duration.ofMillis(DELAY_PER_ITEM_MS));
    }

    @GetMapping("/vociCV-nonBlocking-paged")
    public Flux<VoceCV> getVoceCVFlux(
            final @RequestParam(name = "page") int page, final @RequestParam(name = "size") int size) {
        return voceCVMongoReactiveRepository.retrieveAllVoceCVPaged(
                PageRequest.of(page, size)).delayElements(Duration.ofMillis(DELAY_PER_ITEM_MS));
    }
    
}
