package it.imperato.test.reactor.executabletest;

import it.imperato.test.reactor.model.Player;
import it.imperato.test.reactor.model.PlayerRequest;
import it.imperato.test.reactor.model.PlayerResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReactorRunOnMain {

    private static Logger log  = LogManager.getLogger(ReactorRunOnMain.class);

    private static List<String> squadre = Arrays.asList("Inter","Juventus","Milan","Napoli","Roma");

    private static long TEMPO_CHIAMATA_SERVICE_LEVEL_1 = 1500; // millis
    private static long TEMPO_CHIAMATA_SERVICE_LEVEL_2 = 1000; // millis
    private static int TASK_LEVEL_2_FOREACH_LEVEL_1 = 1; // numero task presenti in 'calculatePlayerTotalScore' con tempo di chiamata definito (level 2)

    public static void main(String[] args) throws Throwable {

        // Esempio di 5 (squadre.size()) chiamate a service (level_1), e 1 chiamata a service legata a ognuna di esse (level_2)
        extractPlayerScore(new Player("Gonzalo","Higuain",null,"Juventus"));

        // wait for secondary threads completion ...
        // System.in.read();
        Thread.currentThread().sleep(20000);
    }

    public static void extractPlayerScore(Player player) {
        List<PlayerRequest> playerInfoRequests=new ArrayList<>();

        // Carriera giocatore:
        for (String squadra : squadre) {
            String serverSquadraUrl = getServerUrlBy(squadra);
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
                    .subscribe(calculatedData -> subscribeAction(calculatedData));

        } catch (Exception e) {
            log.error("ERR: "+e.getMessage());
        }
    }

    // Estrae info per giocatore dalla squadra-i
    private static PlayerResponse extractPlayerInfo(PlayerRequest playerInfoRequests) {
        try {
            String cognome = playerInfoRequests != null ? playerInfoRequests.getCognome() : "-";
            String squadraServerUrl = playerInfoRequests != null ? playerInfoRequests.getRequestUrl() : "-";
            log.info("extractPlayerInfo called: " + cognome);
            log.info("extractPlayerInfo called: " + squadraServerUrl);
            Thread.currentThread().sleep(TEMPO_CHIAMATA_SERVICE_LEVEL_1);
            logCurrentThreadInformation("[extractPlayerInfo]");
            log.info("extractPlayerInfo [result EXTRACTED], by " + squadraServerUrl);
            return new PlayerResponse(playerInfoRequests.getNome(),playerInfoRequests.getCognome(),
                    playerInfoRequests.getDataNascita(),playerInfoRequests.getSquadra(),
                    new BigDecimal(6.5),new BigDecimal(7.5));
        } catch (InterruptedException e) {
            log.error("ERR: "+e.getMessage());
        }
        return null;
    }

    private static PlayerResponse unmarshalInfo(PlayerResponse response) {
        log.info("unmarshalInfo called");
        logCurrentThreadInformation("[unmarshalInfo]");
        // eventuale gestione
        return response;
    }

    // Esegue un calcolo dello score del giocatore
    private static Object calculatePlayerTotalScore(Object documents) {
        try {
            Thread.currentThread().sleep(TEMPO_CHIAMATA_SERVICE_LEVEL_2);
            log.info("calculatePlayerTotalScore called [service]");
            logCurrentThreadInformation("[calculatePlayerTotalScore]");
            // chiamata a service todo
            return new Object();
        } catch (InterruptedException e) {
            log.error("ERR: "+e.getMessage());
        }
        return new Object();
    }

    private static Object subscribeAction(Object response) {
        log.info("subscribeAction called");
        // azione per il subscribe todo
        return new Object();
    }

    private static String getServerUrlBy(String squadra) {
        log.info("getServerUrlBy called");
        return "hostname_"+squadra;
    }

    private static void logCurrentThreadInformation(String msg) {
        log.info(msg + ", thread: " + Thread.currentThread());
    }
}
