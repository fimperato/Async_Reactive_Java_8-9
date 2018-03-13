package it.imperato.test.java8.lambda.t02;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import it.imperato.test.java8.utils.TUtilities;
import it.imperato.test.java8.lambda.t01.City;

public class ComparatorMngOld {

	 private static Logger logger  = LogManager.getLogger(ComparatorMngOld.class);

	 public static void main(String[] args) {
	    	logger.info("ComparatorMng start.");
	    	
	    	List<City> list = TUtilities.getAllCityList();
	    	Collections.sort(list, new Comparator<City>() {

				@Override
				public int compare(City o1, City o2) {
					return o1.getName().compareTo(o2.getName());
				}
			});
	    	logger.info("List sorted is: ");
	    	for (City city : list) {
				logger.info("City : " + city.getName());
			}
	 }
}
