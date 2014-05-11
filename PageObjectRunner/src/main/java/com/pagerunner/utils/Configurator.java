package com.pagerunner.utils;

import org.apache.log4j.Logger;


/**
 * TODO: Do Javadocs for Page Runner
 * Browser Name should be either: IE, FF.  System properties' values are converted toUpper() to standardize them.
 */
//
public class Configurator {
	
	// A constant used to set the implicit wait time throughout the test project
	public static final int DEFAULT_IMPLICIT_WAIT_TIME = 5;
	
	static Configurator configurator = null;
	
	private Logger logger;
	
	public enum BROWSER {FIREFOX, CHROME};
	public enum DESTINATION {LOCAL, REMOTE};

	private String url;
	private String browserName;
    private String remoteEnvironment;
	private BROWSER browser;
	private DESTINATION destination;
	
	public static Configurator get() throws TestException {
		if(configurator == null) {
			configurator = new Configurator();
		}
		return configurator;
	}
	
	private Configurator() throws TestException {

        logger = Logger.getLogger(this.getClass());

        url = System.getProperty("url");
		browserName = System.getProperty("browserName");
        remoteEnvironment = System.getProperty("remoteEnvironment");

		// Retrieve runtime params from command-line for user name, password, URL for the AUT and the browser.
		if(url == null) {
			url = "http://localhost:8080/testhtml/htmltests";
		}

		if(browserName == null) {
            browserName = "FF";
		}
        browserName = browserName.toUpperCase();

		
		// Log the configuration.
		logger.info("Using url: " + url);
		logger.info("Using browserName: " + browserName);
		
		// Verify no required parameters are null.
//		Assert.assertNotNull(url, "Setup error -- URL is null.");
//		Assert.assertNotNull(browserName, "Setup error -- Browser Name is null.");
		
		// Set the Browser.
		if(browserName.equalsIgnoreCase("FF") || browserName.equalsIgnoreCase("Firefox") || browserName.equalsIgnoreCase("FFox"))
    	{
    		browser = BROWSER.FIREFOX;
    	}
		else if (browserName.equalsIgnoreCase("Chrome"))
		{
			browser = BROWSER.CHROME;
		}
		else {
			throw new TestException("Invalid Browser Name specified.");
		}
		
		if (remoteEnvironment == null) {
			destination = DESTINATION.LOCAL;
		}
		else {
			destination = DESTINATION.REMOTE;
		}
		
	}

	
	public String getCmdLineBaseUrl() {
		return url;
	}
	
	public BROWSER getBrowser() {
		return browser;
	}

	public DESTINATION getDestination() {
		return destination;
	}
	
	
}
