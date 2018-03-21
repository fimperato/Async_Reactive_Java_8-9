package it.imperato.test.reactor.executabletest;

import it.imperato.test.reactor.model.*;
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
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ReactorWithFlatMapMain {

    private static Logger log  = LogManager.getLogger(ReactorWithFlatMapMain.class);

    private static List<String> squadre = Arrays.asList("Inter","Juventus","Milan","Napoli","Roma");

    private static long TEMPO_CHIAMATA_SERVICE_LEVEL_1 = 1500; // millis
    private static long TEMPO_CHIAMATA_SERVICE_LEVEL_2 = 1000; // millis
    private static int TASK_LEVEL_2_FOREACH_LEVEL_1 = 1; // numero task presenti in 'calculatePlayerTotalScore' con tempo di chiamata definito (level 2)

    public static void main(String[] args) throws Throwable {

        // Esempio di 5 (squadre.size()) chiamate a service (level_1), e 1 chiamata a service legata a ognuna di esse (level_2)
        extractPlayerScore(new Player("Gonzalo","Higuain",null,"Juventus"));

        // wait for secondary threads completion ...
        // System.in.read();
        Thread.currentThread().sleep(30000);
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
                        .doOnError(e -> log.error("ERROR on step 1: "+e.getMessage()))
                    .map(response -> unmarshalInfo(response))
                        .doOnError(e -> log.error("ERROR on step 2: "+e.getMessage()))
                    .runOn(Schedulers.parallel()) // using the handy runOn() operator, we have to define where parallel threads come from
                    .map(ReactorWithFlatMapMain::calculatePlayerTotalScore)
                        .doOnError(e -> log.error("ERROR on step 3: "+e.getMessage()))
                    .doOnEach(s -> {
                        List<PlayerScore> collect = (s!=null&&s.get()!=null) ? s.get().collect(Collectors.toList()) : new ArrayList<>();
                        log.info("doOnEach stream playscore, result-stream-scored-team size: " + collect.size());
                    })
                    .doOnError(e -> log.error("ERROR on some stream playscore: "+e.getMessage()))
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
    private static PlayersForSquadraResponse extractPlayerInfo(PlayerRequest playerInfoRequests) {
        try {
            String cognome = playerInfoRequests != null ? playerInfoRequests.getCognome() : "-";
            String squadraServerUrl = playerInfoRequests != null ? playerInfoRequests.getRequestUrl() : "-";
            log.info("extractPlayerInfo called: " + cognome);
            log.info("extractPlayerInfo called: " + squadraServerUrl);
            Thread.currentThread().sleep(TEMPO_CHIAMATA_SERVICE_LEVEL_1);
            logCurrentThreadInformation("[extractPlayerInfo]");
            log.info("extractPlayerInfo [result EXTRACTED], by " + squadraServerUrl);
            // il service gli elementi della squadra disponibili
            List<NomeCompleto> nomeCompletoList = new ArrayList<>();
            nomeCompletoList.add(new NomeCompleto(playerInfoRequests.getNome(),"1_"+playerInfoRequests.getCognome()));
            nomeCompletoList.add(new NomeCompleto(playerInfoRequests.getNome(),"2_"+playerInfoRequests.getCognome()));
            nomeCompletoList.add(new NomeCompleto(playerInfoRequests.getNome(),"3_"+playerInfoRequests.getCognome()));
            nomeCompletoList.add(new NomeCompleto(playerInfoRequests.getNome(),"4_"+playerInfoRequests.getCognome()));
            return new PlayersForSquadraResponse(nomeCompletoList,
                    playerInfoRequests.getDataNascita(),playerInfoRequests.getSquadra(),
                    new BigDecimal(6.5),new BigDecimal(7.5));
        } catch (InterruptedException e) {
            log.error("ERR: "+e.getMessage());
        }
        return null;
    }

    private static PlayersForSquadraResponse unmarshalInfo(Object response) {
        log.info("unmarshalInfo called");
        logCurrentThreadInformation("[unmarshalInfo]");
        // eventuale gestione
        return (PlayersForSquadraResponse)response;
    }

    // Esegue un calcolo dello score del giocatore
    private static Stream<PlayerScore> calculatePlayerTotalScore(PlayersForSquadraResponse parsedResponse) {
        try {
            Thread.currentThread().sleep(TEMPO_CHIAMATA_SERVICE_LEVEL_2);
            log.info("calculatePlayerTotalScore called [service]");
            logCurrentThreadInformation("[calculatePlayerTotalScore]");
            // chiamata a service todo

            // in response dal secondo service arriva lista di tutti i giocatori+score per ogni squadra cercata nel primo service
            List<Stream<PlayerScore>> playerScoreListOfStream = parsedResponse.getNomeCompletoList()
                    .stream()
                    .map(res ->
                        { return Stream.of(new PlayerScore(res.getNome(),res.getCognome(),parsedResponse.getSquadra())); })
                    .collect(Collectors.toList());
            // eventuale check sulla lista creata

            // in output: uno stream di PlayerScore
            Stream<PlayerScore> playerScoreStream1 = parsedResponse.getNomeCompletoList()
                    .stream()
                    .flatMap(res ->
                    {
                        return Stream.of(
                                new PlayerScore(res.getNome(), res.getCognome(), parsedResponse.getSquadra())
                        );
                    });

            // in output: uno stream di PlayerScore
            Stream<Stream<PlayerScore>> playerScoreStreamOfStream = parsedResponse.getNomeCompletoList()
                    .stream()
                    .map(res ->
                    {
                        return Stream.of(
                                new PlayerScore(res.getNome(), res.getCognome(), parsedResponse.getSquadra())
                        );
                    });
            Stream<PlayerScore> playerScoreStream2 = playerScoreStreamOfStream.flatMap(Function.identity());

            return playerScoreStream1; // or playerScoreStream2
        } catch (InterruptedException e) {
            log.error("ERR: "+e.getMessage());
        }
        return null;
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
