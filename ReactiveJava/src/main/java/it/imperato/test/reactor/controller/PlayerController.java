package it.imperato.test.reactor.controller;

import it.imperato.test.reactor.executabletest.ReactorMain;
import it.imperato.test.reactor.model.Player;
import it.imperato.test.reactor.model.PlayerRequest;
import it.imperato.test.reactor.model.PlayerResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * To test use: http://[host]:[port]/ReactiveJava/api/playerScore
 *
 */
@RestController
@RequestMapping("/api")
public class PlayerController {

    private static Logger log  = LogManager.getLogger(PlayerController.class);

    private static final int apis_call_port = 8085;
    private static final String apis_call_host = "localhost";

    @Autowired
    RestTemplate restTemplate;

    @GetMapping(path = "/playerScore")
    public String playerScoreGet() {
        Player player = new Player(); // da POST body
        playerScoreLogic(player);
        return "Player score elaboration: successfully completed.";
    }

    /**
     * Eseguire con:
     * curl -d '{"nome":"nome1", "cognome":"cognome1", "squadra":"Juventus"}' -H "Content-Type: application/json" -X POST http://[host]:[port]/ReactiveJava/api/playerScore
     *
     * @param player
     * @return
     */
    @PostMapping(path = "/playerScore", consumes = "application/json", produces = "application/json")
    public PlayerResponse playerScorePost(@RequestBody Player player) {
        playerScoreLogic(player);

        PlayerResponse res =
                new PlayerResponse("Nome","Cognome",null,null,new BigDecimal(6.2),new BigDecimal(6.4)); // test
        return res;
    }

    private void playerScoreLogic(Player player) {
        List<PlayerRequest> playerInfoRequests=new ArrayList<>();
        // Carriera giocatore:
        for (String squadra : ReactorMain.squadre) {
            String serverSquadraUrl = ReactorMain.getServerUrlBy(squadra);
            PlayerRequest request = new PlayerRequest();
            request.setCognome(player.getCognome());
            request.setNome(player.getNome());
            request.setSquadra(player.getSquadraAttuale());
            request.setScoring(true);
            request.setRequestUrl(serverSquadraUrl);
            playerInfoRequests.add(request);
        }

        long startTime = System.nanoTime();

        try {
            Flux<PlayerRequest> fluxRequests = Flux.fromIterable(playerInfoRequests);
            Disposable disposable = fluxRequests.log()
                    .parallel(3) // parallelism value here
                    .map(pir -> extractPlayerInfo(pir))
                    .map(response -> unmarshalInfo(response))
                    .runOn(Schedulers.parallel()) // using the handy runOn() operator, we have to define where parallel threads come from
                    .map(parsedResponse -> calculatePlayerTotalScore(parsedResponse))
                    .doOnComplete(() -> {
                        long endTime = System.nanoTime();
                        double estimatedTime = (double)(endTime - startTime) / 1000000000.0;
                        log.warn("########## TEMPO DI ELABORAZIONE={} ########## [current thread: {}]", estimatedTime,
                                Thread.currentThread().getName());
                    })
                    .subscribe(calculatedData -> log.info("subscribe action/subscriber here"));

        } catch (Exception e) {
            log.error("ERR: "+e.getMessage());
        }
    }

    public PlayerResponse extractPlayerInfo(PlayerRequest playerRequest) {
        // call service to extract item here:
        final String uri = "http://"+apis_call_host+":"+apis_call_port+"/ReactiveJava/anag/anagPlayer";
        //RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject(uri, String.class);
        if(result!=null&&result.contains("OK")) {
            // test (con GET) ok: chiamo il service endpoint per i risultati:
            log.info("[extractPlayerInfo] playerRequest: "+playerRequest);
            String paramSquadraForServer = playerRequest.getRequestUrl().split("_")[1];
            log.info("[extractPlayerInfo] paramSquadraForServer: "+paramSquadraForServer+" ...searching...");
            final String playerAnagUri = "http://"+apis_call_host+":"+apis_call_port
                    +"/ReactiveJava/anag/"+paramSquadraForServer+"/anagPlayer";
            ResponseEntity<PlayerResponse> playerResponseEntity = restTemplate
                    .postForEntity(playerAnagUri, playerRequest, PlayerResponse.class);
            return playerResponseEntity!=null? playerResponseEntity.getBody() : null;
        }
        return null;
    }

    public PlayerResponse calculatePlayerTotalScore(PlayerResponse playerRequest) {
        // call service to extract item here:
        final String uri = "http://"+apis_call_host+":"+apis_call_port+"/ReactiveJava/scoring/scoringCalculate";

        String result = restTemplate.getForObject(uri, String.class);
        if(result!=null&&result.contains("OK")) {
            // test (con GET) ok: chiamo il service endpoint per i risultati:
            final String playerAnagUri = "http://"+apis_call_host+":"+apis_call_port+"/ReactiveJava/scoring/scoringCalculate";
            ResponseEntity<PlayerResponse> playerResponseEntity = restTemplate
                    .postForEntity(playerAnagUri, playerRequest, PlayerResponse.class);
            return playerResponseEntity!=null? playerResponseEntity.getBody() : null;
        }
        return null;
    }

    // To move in utils class:
    public static PlayerResponse unmarshalInfo(PlayerResponse response) {
        log.info("unmarshalInfo called");
        logCurrentThreadInformation("[unmarshalInfo]");
        // eventuale gestione
        return response;
    }

    private static void logCurrentThreadInformation(String msg) {
        log.info(msg + ", thread: " + Thread.currentThread());
    }
    // END To move in utils class
}
