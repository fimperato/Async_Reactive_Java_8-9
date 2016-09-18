package it.imperato.test.java8.lambda.t01.oldapproach;

import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import it.imperato.test.java8.lambda.TUtilities;
import it.imperato.test.java8.lambda.t01.City;
 
public class AnalysisManager {
 
	private Logger logger = LogManager.getLogger(this.getClass().getName());
    
    public Set<City> getLargestCities(){
    	Set<City> largest = new HashSet<>();
    	for (City c : TUtilities.getAllCityList())
    		if (c.getArea() > 250)
    			largest.add(c);
    	
    	return largest;
    }
    
    public Set<City> getIntermediatePopulatedCities(){
    	Set<City> intermediate = new HashSet<>();
    	for (City c : TUtilities.getAllCityList())
    		if (c.getNumInhabitants() > 150000 && c.getNumInhabitants() < 200000)
    			intermediate.add(c);
    	
    	return intermediate;
    }
    
    // other conditions method
    
    public void doAnalysis(Set<City> cities){
    	
    	// analysis logic ...
    	
    	logger.info("Analysis result: " + cities.size() + ".");
    }
    
    public static void main(String[] args) {
    	// log init
    	AnalysisManager am = new AnalysisManager(); 
      
    	am.doAnalysis(am.getLargestCities());
    	am.doAnalysis(am.getIntermediatePopulatedCities());
      
    } 
 
}