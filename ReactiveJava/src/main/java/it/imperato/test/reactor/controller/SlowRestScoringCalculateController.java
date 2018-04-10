package it.imperato.test.reactor.controller;

import it.imperato.test.reactor.model.PlayerResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.concurrent.ThreadLocalRandom;

@RestController
@RequestMapping("/scoring")
public class SlowRestScoringCalculateController {

    private static Logger log  = LogManager.getLogger(SlowRestScoringCalculateController.class);

    @GetMapping(path = "/scoringCalculate")
    public String scoringCalculateGet() {
        try {
            // chiamata get di test
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "scoringCalculate-OK";
    }

    @PostMapping(path = "/scoringCalculate")
    public PlayerResponse scoringCalculatePost(@RequestBody PlayerResponse playerResponse) {
        log.info("[scoringCalculate] Init.. with playerResponse: "+playerResponse);
        float maxValueExclusive = 8f;
        float maxValueWithBonusExclusive = 15.5f;
        try {
            // result: PlayerResponse con gli scoring richiesti (e nome thread)
            playerResponse.setAverageScore(new BigDecimal( Float.toString(ThreadLocalRandom.current().nextFloat()*maxValueExclusive) )); // (random) scoring by 2nd server: average
            playerResponse.setAverageScoreWithBonus(new BigDecimal( ThreadLocalRandom.current().nextFloat()*maxValueWithBonusExclusive )); // (random) scoring by 2nd server: average with bonus
            playerResponse.setCognome(playerResponse.getCognome()+"_"+Thread.currentThread().getName());
            Thread.sleep(2000);
            log.info("[scoringCalculate] result by rest service (playerResponse): "+playerResponse);
        } catch (Exception e) {
            log.error("PostMapping [scoringCalculatePost] ERR: "+e.getMessage(),e);
        }
        return playerResponse;
    }

}
