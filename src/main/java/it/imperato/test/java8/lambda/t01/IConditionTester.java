package it.imperato.test.java8.lambda.t01;

@FunctionalInterface
public interface IConditionTester<T> {
	
	public boolean test(T t);   
	
}
