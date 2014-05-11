package com.pagerunner.utils;

import org.apache.log4j.Logger;
import org.testng.Assert;


/**
 * Set configuration parameters for running tests.  Specifically:
 * Browser name: Reads this from the command-line parameter -DbrowserName.  Possible values: "Firefox", "FF", or "FFox"
 * or "Chrome" - case insensitive.
 * Url: The page to run the test against.  This is configured as a parameter to the test itself since each test class
 * will run tests for a different page object and therefore, against a different test page.  The url is set within the
 * Configurator from the TestBase class and stored with in the Configurator for later use.
 * @author pgrandje
 * @date 05/11/2014
 */
public class Configurator {
	
	// A constant used to set the implicit wait time throughout the test project
	public static final int DEFAULT_IMPLICIT_WAIT_TIME = 5;
	
	static Configurator configurator = null;
	
	private Logger logger;
	
	public enum BROWSER {FIREFOX, CHROME};

    private String url;
	private String browserName;
	private BROWSER browser;
	
	public static Configurator get() {
		if(configurator == null) {
			configurator = new Configurator();
		}
		return configurator;
	}
	
	private Configurator() {

        logger = Logger.getLogger(this.getClass());

//        url = System.getProperty("url");
		browserName = System.getProperty("browserName");

		// if the url for the test was not supplied, throw an exception.
//		if(url == null) {
//			throw new TestException("URL not supplied.");
//		}

        // Default to Firefox if the browser isn't specified.
		if(browserName == null) {
            browserName = "FF";
		}

		// Log the configuration.
		logger.info("Using url: " + url);
		logger.info("Using browserName: " + browserName);
		
		// This isn't necessary, but it's an extra assurance that no required parameters are null.
		Assert.assertNotNull(browserName, "Setup error -- Browser Name is null.");
		
		// Set the Browser.
        // TODO: Set these browser strings to string constant member.
		if(browserName.equalsIgnoreCase("FF") || browserName.equalsIgnoreCase("Firefox") || browserName.equalsIgnoreCase("FFox"))
    	{
    		browser = BROWSER.FIREFOX;
    	}
		else if (browserName.equalsIgnoreCase("Chrome"))
		{
			browser = BROWSER.CHROME;
		}
		else {
            /* Using a RunTimeException allows me to not have to throw this from the test classes, which allows not having
               to specify an explicit constructor in each test class.
            */
			throw new RuntimeException("Error creating Configuration--Invalid Browser Name specified.");
		}
		
	}

	public String getUrl() {
		return url;
	}

    public void setUrl(String url) {
        this.url = url;
    }

	public BROWSER getBrowser() {
		return browser;
	}
	
}
