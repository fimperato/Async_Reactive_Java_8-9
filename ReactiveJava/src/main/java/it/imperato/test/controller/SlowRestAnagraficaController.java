package it.imperato.test.controller;

import it.imperato.test.reactor.model.PlayerRequest;
import it.imperato.test.reactor.model.PlayerResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

@RestController
@RequestMapping("/anag")
public class SlowRestAnagraficaController {

    private static Logger log  = LogManager.getLogger(SlowRestAnagraficaController.class);

    @Autowired
    RestTemplate restTemplate;

    @GetMapping(path = "/anagPlayer")
    public String anagPlayerGet() {
        try {
            // chiamata get di test
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "anagPlayer-OK";
    }

    @PostMapping(path = "/{squadra}/anagPlayer")
    public PlayerResponse anagPlayerPost(@RequestBody PlayerRequest playerRequest, @PathVariable String squadra) {
        PlayerResponse playerResponse = null;
        log.info("[anagPlayerPost] Init.. with playerRequest: "+playerRequest+ " and Squadra: "+squadra);
        try {
            // result: PlayerResponse con dentro le anagrafiche richieste (e nome thread)
            Date dataNascitaAnag = null;
            String existingPlayerInSquadra = null;
            if(ThreadLocalRandom.current().nextInt(2)>0) {
                dataNascitaAnag = playerRequest.getDataNascita();
                existingPlayerInSquadra = squadra; // player nella {squadra} in carriera
            }
            playerResponse = new PlayerResponse(
                    playerRequest.getNome()+"_"+Thread.currentThread().getName(),
                    playerRequest.getCognome(), dataNascitaAnag, existingPlayerInSquadra,
                    null, null);
            Thread.sleep(3000);
            log.info("[anagPlayerPost] result by rest service (playerResponse): "+playerResponse);
        } catch (Exception e) {
            log.error("PostMapping [anagPlayerPost] ERR: "+e.getMessage(),e);
        }
        return playerResponse;
    }

}
