package it.imperato.test.java8.lambda.t03;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import it.imperato.test.java8.lambda.TUtilities;
import it.imperato.test.java8.lambda.t01.City;
import it.imperato.test.java8.lambda.t01.IConditionTester;
 
public class AnalysisMngMethodRef {
 
	private Logger logger = LogManager.getLogger(this.getClass().getName());

	public Set<City> getFilteredCitiesByCondition(Predicate<City> tester){
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
    
    private static Logger log  = LogManager.getLogger(AnalysisMngMethodRef.class);
    
    public static void main(String[] args) {
    	log.info("AnalysisManager start.");
    	AnalysisMngMethodRef am = new AnalysisMngMethodRef(); 
      
    	Predicate<City> intermediatePopulatedTester = 
    			c -> c.getNumInhabitants() > 150000 && c.getNumInhabitants() < 200000;
    	
    	log.info("largestCityTester: ");
    	LargeCityCondition lcc = new LargeCityCondition();
    	am.doAnalysis(am.getFilteredCitiesByCondition( lcc::conditionBy));
    	log.info("intermediatePopulatedTester: ");
    	am.doAnalysis(am.getFilteredCitiesByCondition( intermediatePopulatedTester ));
      
    } 
 
}