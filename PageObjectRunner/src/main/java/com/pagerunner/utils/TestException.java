package com.pagerunner.utils;


public class TestException extends Exception {
	
	public TestException(String errorMessage) {
		super("TEST FAILURE -- " + errorMessage);	
	}

}
