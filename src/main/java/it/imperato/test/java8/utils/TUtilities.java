package it.imperato.test.java8.utils;

import java.util.Arrays;
import java.util.List;

import it.imperato.test.java8.lambda.t01.City;

public class TUtilities {

	public static List<City> getAllCityList() {
		List<City> cities = Arrays.asList(
	    		new City("Piacenza", "033", 102191, 118, "Sant'Antonino"),
	    		new City("Parma", "034", 193811, 260, "Sant'Ilario"),
	    		new City("Reggio Emilia", "035", 171234, 230, "San Prospero"),
	    		new City("Modena", "036", 184973, 183, "San Geminiano"),
	    		new City("Bologna", "037", 387554, 160, "San Petronio"),
	    		new City("Ferrara", "038", 132459, 404, "San Giorgio"),
	    		new City("Ravenna", "039", 164854, 653, "Sant'Apollinare")
	    );
		return cities;
	}
	
}
