package com.testhelper.utils;

import org.apache.log4j.Logger;



// Browser Name should be either: IE, FF, Chrome, HTMLUNIT.  Strings are converted toUpper() to standardize them.
// 
public class Configurator {
	
	// A constant used to set the implicit wait time throughout the test project
	public static final int DEFAULT_IMPLICIT_WAIT_TIME = 5;
	
	static Configurator configurator = null;
	
	private Logger logger;
	
	public enum BROWSER { FIREFOX, IE, CHROME, SAFARI, HTMLUNIT };
	public enum DESTINATION {LOCAL, REMOTE};

	private String url;
	private String browserName;
	private BROWSER browser;
	private String remoteEnvironment;
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
		if(browserName.equalsIgnoreCase("IE") || browserName.equalsIgnoreCase("INTERNET EXPLORER"))  
    	{
			browser = BROWSER.IE;
    	}
    	else if(browserName.equalsIgnoreCase("FF") || browserName.equalsIgnoreCase("Firefox") || browserName.equalsIgnoreCase("FFox"))
    	{
    		browser = BROWSER.FIREFOX;
    	}
		else if (browserName.equalsIgnoreCase("Chrome"))
		{
			browser = BROWSER.CHROME;
		}
		// NOTE:  Safari not supported yet by WebDriver
		else if (browserName.equalsIgnoreCase("Safari"))
		{
			browser = BROWSER.SAFARI;
			throw new TestException("Safari not yet supported by WebDriver.");
		}
		else if (browserName.equalsIgnoreCase("HTML") || browserName.equalsIgnoreCase("HTMLUNIT") )
		{
			browser = BROWSER.HTMLUNIT;	
		}
		else {
			throw new TestException("Invalid Browser Name passed from command-line.");
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
	
	public String getCmdLineBrowserName() {
		return browserName;
	}
	
	public BROWSER getBrowser() {
		return browser;
	}
	
	public String getRemoteEnvironment() {
		return remoteEnvironment;
	}	


	public DESTINATION getDestination() {
		return destination;
	}

	public final String getUrl() {
		return url;
	}

	public final void setUrl(String url) {
		this.url = url;
	}

	public final String getBrowserName() {
		return browserName;
	}

	public final void setBrowserName(String browserName) {
		this.browserName = browserName;
	}
	
	
}
