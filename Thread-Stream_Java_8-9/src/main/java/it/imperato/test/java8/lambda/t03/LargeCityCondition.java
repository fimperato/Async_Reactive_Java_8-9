package it.imperato.test.java8.lambda.t03;

import it.imperato.test.java8.lambda.t01.City;

public class LargeCityCondition {

	public boolean conditionBy(City c) { 
		return c.getArea() > 250;
	}
	
}
