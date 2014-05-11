package com.pagerunner.utils;

import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;


/**
 * Assigns starts the WebDriver, opens the requested browser and loads the test app into it.
 * @return Webdriver Instance
 */
public class DriverManager {
	
	
	// Determines which browser and loads it.  At the end it sends the base URL to retrieve the starting web page.
	public static synchronized WebDriver getDriver(Configurator configurator) throws TestException
    {
		final Logger logger;
    	
    	WebDriver driver = null;
    	
    	logger = Logger.getLogger(DriverManager.class.getName());
		
    	// First process REMOTE browsers
    	if (configurator.getDestination() == Configurator.DESTINATION.REMOTE && configurator.getBrowser() == Configurator.BROWSER.FIREFOX) {
    		
    		logger.info("Setting WebDriver to run FIREFOX on a REMOTEHOST.");
    		DesiredCapabilities capabilities = DesiredCapabilities.firefox();
    		
    		try {
				driver = new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), capabilities);
			} catch (MalformedURLException badUrlException) {
				throw new TestException("Bad Url for the RemoteWebDriver's Selenium-Grid hub.");
			}
    	}
		else if (configurator.getDestination() == Configurator.DESTINATION.REMOTE && configurator.getBrowser() == Configurator.BROWSER.CHROME)
		{
			logger.info("Setting WebDriver to run CHROME on a REMOTEHOST.");
			throw new TestException("We're not using ChromeDriver at this time.  We saw errors using ChromeDriver and noticed Selenium defects were filed against it.");
		}
    	// *** these now process running on LOCALHOST ****
    	else if (configurator.getBrowser() == Configurator.BROWSER.FIREFOX) {
    		
    		logger.info("Setting WebDriver to FIREFOX on LOCALHOST.");
    		
    		// We're using an explicit Firefox profile so we can use FireBug when debugging tests.
    		// TODO:  We should make the Firefox profile a command-line param.
//    		ProfilesIni allProfiles = new ProfilesIni();
//    		FirefoxProfile profile = allProfiles.getProfile("SeleniumFFoxProfile");
    		// We're not using preferences, but I wanted the example in here just in case we need it later.
    		// profile.setPreference("foo.bar", 23);
//    		driver = new FirefoxDriver(profile);
    		
    		// This was the old way I did this before I added the ability for an FFox profile.
    		// The above method didn't seem to work with FFox 13
    		driver = new FirefoxDriver();
    	}
		else if (configurator.getBrowser() == Configurator.BROWSER.CHROME)
		{
			logger.info("Setting WebDriver to CHROME on LOCALHOST.");
			driver = new ChromeDriver();
        }
    	else {
    		throw new TestException("UNKNOWN BROWSER in Configurator.");
    	}
    	
    	// TODO:  Should I relocate retrieval of the URL?  This class should be just managing the browser, shouldn't it?
    	//			Could even rename it to BrowserManager.
    	driver.get(configurator.getCmdLineBaseUrl());
    	
		return driver;
    }
    
}
