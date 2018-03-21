package it.imperato.test.reactor.executabletest;

import it.imperato.test.reactor.model.Player;
import it.imperato.test.reactor.model.PlayerRequest;
import it.imperato.test.reactor.model.PlayerResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReactorMain {

    private static Logger log  = LogManager.getLogger(ReactorMain.class);

    public static List<String> squadre = Arrays.asList("Inter","Juventus","Milan","Napoli","Roma");

    private static double totalElaborationTimeExpected;
    private static long TEMPO_CHIAMATA_SERVICE_LEVEL_1 = 1500; // millis
    private static long TEMPO_CHIAMATA_SERVICE_LEVEL_2 = 1000; // millis
    private static int TASK_LEVEL_2_FOREACH_LEVEL_1 = 1; // numero task presenti in 'calculatePlayerTotalScore' con tempo di chiamata definito (level 2)

    public static void main(String[] args) throws Throwable {

        // Esempio di 5 (squadre.size()) chiamate a service (level_1), e 1 chiamata a service legata a ognuna di esse (level_2)
        int chiamateAServiceLevel_1 = squadre.size();
        int chiamateAServiceLevel_2 = squadre.size(); // pari a squadre.size perchè avviene in maniera sequenziale al livello superiore di task
        totalElaborationTimeExpected = (chiamateAServiceLevel_1*TEMPO_CHIAMATA_SERVICE_LEVEL_1
                + chiamateAServiceLevel_2*TASK_LEVEL_2_FOREACH_LEVEL_1*TEMPO_CHIAMATA_SERVICE_LEVEL_2) / 1000.0;
        extractPlayerScore(new Player("Gonzalo","Higuain",null,"Juventus"));

        // Esempi di chiamate con reactor framework element:
        exampleWithOnlyMap();

        exampleWithPublishOn();

        exampleWithParallelAndRunOn();

        // wait for secondary threads completion ...
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
                    .map(pir -> extractPlayerInfo(pir))
                    .map(response -> unmarshalInfo(response))
                    .map(parsedResponse -> calculatePlayerTotalScore(parsedResponse))
                    .doOnComplete(() -> {
                        log.info("current thread: "+Thread.currentThread().getName());
                        long endTime = System.nanoTime();
                        double estimatedTime = (double)(endTime - startTime) / 1000000000.0;
                        log.warn("########## TEMPO DI ELABORAZIONE={} ##########", estimatedTime);
                        log.info("@@@ Differenza tra tempo atteso e reale={}; percentuale delta: {}% @@@",
                                (estimatedTime - totalElaborationTimeExpected),
                                (estimatedTime - totalElaborationTimeExpected)*100/estimatedTime);
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
        // eventuale gestione
        return response;
    }

    // Esegue un calcolo dello score del giocatore
    private static Object calculatePlayerTotalScore(Object parsedResponse) {
        try {
            Thread.currentThread().sleep(TEMPO_CHIAMATA_SERVICE_LEVEL_2);
            log.info("calculatePlayerTotalScore called [service]");
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

    public static String getServerUrlBy(String squadra) {
        log.info("getServerUrlBy called");
        return "hostname_"+squadra;
    }

    public static void exampleWithOnlyMap() {
        Mono.just("[exampleWithOnlyMap]")
                .repeat(2)
                .map(n -> {
                    logCurrentThreadInformation(n + "_map");
                    return n;
                })
                .subscribe(n -> logCurrentThreadInformation(n + "_subscribe"));
    }

    public static void exampleWithPublishOn() {
        Mono.just("[exampleWithPublishOn]")
                .repeat(2)
                .publishOn(Schedulers.parallel()) //.publishOn(Schedulers.single())
                .map(n -> {
                    try {
                        Thread.currentThread().sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    logCurrentThreadInformation(n + "_map");
                    return n;
                })
                .subscribe(n -> logCurrentThreadInformation(n + "_subscribe"));
    }

    public static void exampleWithParallelAndRunOn() {
        Mono.just("[exampleWithParallelAndRunOn]")
                .repeat(2)
                .parallel()
                .runOn(Schedulers.parallel())
                .map(n -> {
                    logCurrentThreadInformation(n + "_map");
                    return n;
                })
                .subscribe(n -> logCurrentThreadInformation(n + "_subscribe"));
    }

    private static void logCurrentThreadInformation(String msg) {
        log.info(msg + ", thread: " + Thread.currentThread());
    }
}
