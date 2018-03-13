package it.imperato.test.java8.parallelism.t01;

import it.imperato.test.java8.lambda.t01.City;
import it.imperato.test.java8.utils.TUtilities;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

public class ParallelismJava {

    private static Logger log  = LogManager.getLogger(ParallelismJava.class);

    public static void main(String[] args) {

        List<City> list = TUtilities.getAllCityList();
        List<City> bigCityList = new ArrayList<>();
        list.stream().forEach(
                city -> { bigCityList.add(city); log.info("1: "+city.getName());
                    bigCityList.add(city.clone()); bigCityList.get(bigCityList.size()-1).setName(bigCityList.get(bigCityList.size()-1).getName()+"_b"); log.info("2: "+city.getName());
                    bigCityList.add(city.clone()); bigCityList.get(bigCityList.size()-1).setName(bigCityList.get(bigCityList.size()-1).getName()+"_c"); log.info("3: "+city.getName()); });

        bigCityList.stream().forEach(
                a -> log.info(a + ": " + Thread.currentThread().getName()) );

        log.info("### parallel:");
        bigCityList.stream().parallel().forEach(
                a -> log.info(a + ": " + Thread.currentThread().getName()) );

        log.info("### parallelStream:");
        bigCityList.parallelStream().forEach(
                a -> log.info(a + ": " + Thread.currentThread().getName()) );

        log.info("### ForkJoin common pool size: " + ForkJoinPool.commonPool().getParallelism());
        log.info("### ### ### ### ###");
        log.info("### ### ### ### ###");

        log.info("### Using map:");
        List<String> cityNames = list.stream()
                .map(City::getName)
                .collect(Collectors.toList());
        cityNames.stream().forEach(a -> log.info(a));

        log.info("### Using map [2]:");
        int nCity=3;
        list.stream()
                .filter(a -> a.getName()!=null)
                .flatMap(a -> a.getComuniProvincia().stream()) // flat n-stream (nCity) in un unico stream
                .sorted()
                .limit(nCity)
                .forEach(a -> log.info(a));
    }

}
