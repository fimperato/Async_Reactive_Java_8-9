package it.imperato.test.utils.thread;

import java.util.concurrent.Callable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import it.imperato.test.parallelprogramming.forkjoin.ParellelVsSeqMaximumFinder;

public class TestBaseThread implements Callable<String> {

	protected static Logger log = LogManager.getLogger(ParellelVsSeqMaximumFinder.class);
	
    @Override
    public String call(){
        try {
			Thread.sleep(2000);
			return "value-100";
		} catch (InterruptedException e) {
			log.error("ERROR in TestBaseThread call "+e.getMessage());
		}
        return "N.D.";
    }

}
