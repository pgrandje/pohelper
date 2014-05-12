package com.pagerunner.utils;

/**
 * Exception class used to identify exception specific to the Page Object Tester module.
 * @author Paul Grandjean
 */
public class TestException extends Exception {
	
	public TestException(String errorMessage) {
		super("TEST FAILURE -- " + errorMessage);	
	}

}
