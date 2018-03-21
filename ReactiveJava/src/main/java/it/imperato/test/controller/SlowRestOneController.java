package it.imperato.test.controller;

import it.imperato.test.reactor.executabletest.ReactorMain;
import it.imperato.test.reactor.model.Player;
import it.imperato.test.reactor.model.PlayerRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/slowrest")
public class SlowRestOneController {

    private static Logger log  = LogManager.getLogger(SlowRestOneController.class);

    @Autowired
    RestTemplate restTemplate;

    @GetMapping(path = "/myrest")
    public String myrest() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "OK";
    }

    @GetMapping(path = "/emulateMultipleCall")
    public String emulateMultipleCall() {
        Player player = new Player(); // da POST body
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
                    .map(pir -> extractPlayerInfo(pir))
                    .doOnComplete(() -> {
                        log.info("current thread: "+Thread.currentThread().getName());
                        long endTime = System.nanoTime();
                        double estimatedTime = (double)(endTime - startTime) / 1000000000.0;
                    })
                    .subscribe(calculatedData -> log.info("subscribe action/subscriber here"));

        } catch (Exception e) {
            log.error("ERR: "+e.getMessage());
        }
        return "END emulateMultipleCall";
    }

    public String extractPlayerInfo(PlayerRequest playerRequest) {
        // call service to extract item here:
        final String uri = "http://localhost:8085/ReactiveJava/slowrest/myrest";

        //RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject(uri, String.class);
        return "item extracted here";
    }
}
