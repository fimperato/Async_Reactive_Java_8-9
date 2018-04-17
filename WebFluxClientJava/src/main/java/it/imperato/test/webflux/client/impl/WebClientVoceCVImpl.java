package it.imperato.test.webflux.client.impl;

import it.imperato.test.webflux.client.WebClientVoceCV;
import it.imperato.test.webflux.model.VoceCV;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class WebClientVoceCVImpl implements WebClientVoceCV {

    private final WebClient webClient;

    public WebClientVoceCVImpl(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                //.defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.TEXT_EVENT_STREAM_VALUE)
                .baseUrl("http://localhost:8081/api/cv")
                .build();
    }

    @Override
    public Mono<VoceCV> getById(Long id) {

        return this.webClient.get().uri("/{id}", id)
                .retrieve().bodyToMono(VoceCV.class);
    }

    @Override
    public Flux<VoceCV> getAll() {

        return webClient
                .get().uri("/vociCV-nonBlocking")
                .retrieve()
                .bodyToFlux(VoceCV.class);
    }

    @Override
    public Mono<VoceCV> save(VoceCV voceCV) {

        return webClient.post().uri("/post")
                .body(BodyInserters.fromObject(voceCV))
                .exchange().flatMap( clientResponse -> clientResponse.bodyToMono(VoceCV.class) );
    }

    @Override
    public Mono<VoceCV> update(Long id, VoceCV voceCV) {

        return webClient.put().uri("/put/{id}", id)
                .body(BodyInserters.fromObject(voceCV))
                .exchange().flatMap(clientResponse -> clientResponse.bodyToMono(VoceCV.class));
    }

    @Override
    public Mono<Void> delete(Long id) {

        return webClient.delete().uri("/delete/{id}", id)
                .retrieve().bodyToMono(Void.class);
    }

}
