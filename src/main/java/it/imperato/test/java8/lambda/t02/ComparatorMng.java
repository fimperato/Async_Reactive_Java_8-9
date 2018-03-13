package it.imperato.test.java8.lambda.t02;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import it.imperato.test.java8.utils.TUtilities;
import it.imperato.test.java8.lambda.t01.City;

public class ComparatorMng {

	 private static Logger logger  = LogManager.getLogger(ComparatorMng.class);

	 public static void main(String[] args) {
	    	logger.info("ComparatorMng start.");
	    	
	    	List<City> list = TUtilities.getAllCityList();
	    	
//	    	list.sort((City o1, City o2) -> o1.getName().compareTo(o2.getName()));
	    	list.sort((o1, o2) -> o1.getName().compareTo(o2.getName()));
	    	logger.info("List sorted is: ");
	    	list.forEach( city -> logger.info("City : " + city.getName()) );
	    	
	    	list.sort((o1, o2) 
	    			-> {logger.info("ordering "+o1.getName()); return o1.getArea() - o2.getArea();} );
	    	logger.info("List sorted by  is: ");
	    	list.forEach( city -> logger.info("City: " + city.getName() + " area:" + city.getArea()) );
	 }
}
