package it.imperato.test.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/scoring")
public class SlowRestScoringCalculateController {

    private static Logger log  = LogManager.getLogger(SlowRestScoringCalculateController.class);

    @GetMapping(path = "/scoringCalculate")
    public String scoringCalculate() {
        try {
            // result: PlayerResponse con dentro gli scoring richiesti (e nome thread)
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "scoringCalculate-OK";
    }

}
