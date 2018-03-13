package it.imperato.test.java8.lambda.t01.newapproach;

import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import it.imperato.test.java8.utils.TUtilities;
import it.imperato.test.java8.lambda.t01.City;
import it.imperato.test.java8.lambda.t01.IConditionTester;
 
public class AnalysisManager {
 
	private Logger logger = LogManager.getLogger(this.getClass().getName());

	public Set<City> getFilteredCitiesByCondition(IConditionTester<City> tester){
    	Set<City> largest = new HashSet<>();
    	for (City c : TUtilities.getAllCityList())
    		if (tester.test(c))
    			largest.add(c);
    	
    	return largest;
    }

    public void doAnalysis(Set<City> cities){
    	
    	// analysis logic ...  
    	
    	logger.info("Analysis result: " + cities.size() + ".");
    }
    
    private static Logger log  = LogManager.getLogger(AnalysisManager.class);
    
    public static void main(String[] args) {
    	log.info("AnalysisManager start.");
    	AnalysisManager am = new AnalysisManager(); 
      
    	IConditionTester<City> largestCityTester = c -> c.getArea() > 250;
    	IConditionTester<City> intermediatePopulatedTester = c -> c.getNumInhabitants() > 150000 && c.getNumInhabitants() < 200000;
    	
    	log.info("largestCityTester: ");
    	am.doAnalysis(am.getFilteredCitiesByCondition( largestCityTester ));
    	log.info("intermediatePopulatedTester: ");
    	am.doAnalysis(am.getFilteredCitiesByCondition( intermediatePopulatedTester ));
      
    } 
 
}