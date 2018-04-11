package it.imperato.test.webflux.repository;

import it.imperato.test.webflux.model.VoceCV;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface VoceCVMongoNonBlockingRepo extends ReactiveCrudRepository<VoceCV, String> {

    @Query("{ : { $exists: true }}")
    Flux<VoceCV> retrieveAllVoceCVPaged(final Pageable page);

}
