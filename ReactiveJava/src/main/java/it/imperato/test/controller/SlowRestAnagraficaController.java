package it.imperato.test.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/anag")
public class SlowRestAnagraficaController {

    private static Logger log  = LogManager.getLogger(SlowRestAnagraficaController.class);

    @Autowired
    RestTemplate restTemplate;

    @GetMapping(path = "/anagPlayer")
    public String anagPlayer() {
        try {
            // result: PlayerResponse con dentro le anagrafiche richieste (e nome thread)
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "anagPlayer-OK";
    }

}
