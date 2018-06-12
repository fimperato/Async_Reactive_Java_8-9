package it.imperato.test.utils;

import java.math.BigInteger;

public class CalcUtils {
	
	/**
	 * Esegue il calcolo fattoriale di x int
	 * 
	 * @param x
	 * @return
	 */
	public static int calculateFactorial(int x)
    {
		if (Math.abs(x) == 1) {
            return x;
        } else{
            if(x > 1)
                return x * calculateFactorial(x-1);
            else
            {
                return x * calculateFactorial(x+1);
            }
        }
    }
	
	/**
	 * Esegue il calcolo fattoriale di x BigInteger
	 * 
	 * @param x
	 * @return
	 */
	public static BigInteger calculateFactorial(BigInteger x)
    {
		if (BigInteger.ONE.equals(x.abs())) {
            return x;
        } else{
            if(x.compareTo(BigInteger.ONE)>0)
                return x.multiply( calculateFactorial(x.subtract(BigInteger.ONE)) );
            else
            {
                return x.multiply( calculateFactorial(x.add(BigInteger.ONE)) );
            }
        }
    }
	
}
