package it.imperato.test.parallelprogramming.config;

import it.imperato.test.asyncutils.PPUtils;

/**
 * Project setup
 * 
 */
public final class Setup {

    private Setup() {
    	
    }

    /**
     * Setup method
     * 
     * @param input val
     * @return output int
     */
    public static int setup(final int val) {
        final int[] result = new int[1];
        
        PPUtils.finish(() -> {
        	PPUtils.async(() -> {
                result[0] = val;
            });
        });
        
        return result[0];
    }
}
