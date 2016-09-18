package it.imperato.test.java8.lambda.t01.oldapproach;

import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import it.imperato.test.java8.lambda.t01.City;
import it.imperato.test.java8.lambda.t01.IConditionTester;
import it.imperato.test.java8.lambda.t01.T01Utilities;
 
public class AnalysisManagerInnerAnonymousClass {
 
	private Logger logger = LogManager.getLogger(this.getClass().getName());

    public Set<City> getFilteredCitiesByCondition(IConditionTester<City> tester){
    	Set<City> largest = new HashSet<>();
    	for (City c : T01Utilities.getAllCityList())
    		if (tester.test(c))
    			largest.add(c);
    	
    	return largest;
    }

    public void doAnalysis(Set<City> cities){
    	
    	// analysis logic ...  
    	
    	logger.info("Analysis result: " + cities.size() + ".");
    }
    
    public static void main(String[] args) {
    	AnalysisManagerInnerAnonymousClass am = new AnalysisManagerInnerAnonymousClass(); 
      
    	am.doAnalysis(am.getFilteredCitiesByCondition(new IConditionTester<City>() {
			
			@Override
			public boolean test(City c) {
				return (c.getArea() > 250);
			}
		}));
    	am.doAnalysis(am.getFilteredCitiesByCondition(new IConditionTester<City>() {
			
			@Override
			public boolean test(City c) {
				return (c.getNumInhabitants() > 150000 && c.getNumInhabitants() < 200000);
			}
		}));
      
    } 
 
}