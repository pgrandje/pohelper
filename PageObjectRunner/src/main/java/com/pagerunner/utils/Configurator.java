package com.pagerunner.utils;

import org.apache.log4j.Logger;
import org.testng.Assert;


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

	private String url;
	private String browserName;
	private BROWSER browser;
	
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

		// if the url for the test was not supplied, throw an exception.
		if(url == null) {
			throw new TestException("URL not supplied.");
		}

        // Default to Firefox if the browser isn't specified.
		if(browserName == null) {
            browserName = "FF";
		}
        browserName = browserName.toUpperCase();

		
		// Log the configuration.
		logger.info("Using url: " + url);
		logger.info("Using browserName: " + browserName);
		
		// This isn't necessary, but it's an extra assurance that no required parameters are null.
		Assert.assertNotNull(url, "Setup error -- URL is null.");
		Assert.assertNotNull(browserName, "Setup error -- Browser Name is null.");
		
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
		
	}

	public String getUrl() {
		return url;
	}
	
	public BROWSER getBrowser() {
		return browser;
	}
	
}
