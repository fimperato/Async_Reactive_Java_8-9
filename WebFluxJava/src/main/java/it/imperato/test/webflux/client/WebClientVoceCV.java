package it.imperato.test.webflux.client;

import it.imperato.test.webflux.model.VoceCV;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface WebClientVoceCV {

    Mono<VoceCV> getById(Long id);

    Flux<VoceCV> getAll();

    Mono<VoceCV> save(VoceCV voceCV);

    Mono<VoceCV> update(Long id, VoceCV voceCV);

    Mono<Void> delete(Long id);

}
