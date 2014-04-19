package com.selgentests.utils;

import com.selgentests.utils.Configurator.BROWSER;
import com.selgentests.utils.Configurator.DESTINATION;
import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.internal.ProfilesIni;
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
    	if (configurator.getDestination() == DESTINATION.REMOTE && configurator.getBrowser() == BROWSER.FIREFOX) {
    		
    		logger.info("Setting WebDriver to run FIREFOX on a REMOTEHOST.");
    		DesiredCapabilities capabilities = DesiredCapabilities.firefox();
    		
    		try {
				driver = new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), capabilities);
			} catch (MalformedURLException badUrlException) {
				throw new TestException("Bad Url for the RemoteWebDriver's Selenium-Grid hub.");
			}
    	}
    	else if(configurator.getDestination() == DESTINATION.REMOTE && configurator.getBrowser() == BROWSER.IE) {
    			
    		logger.info("Setting WebDriver to run IE on a REMOTEHOST.");
	    	DesiredCapabilities capabilities = DesiredCapabilities.internetExplorer();
//	    	capabilities.setPlatform(Platform.WINDOWS);
    		// not certain why we use this but vivek had this in there originally--it's worthy of research.
	    	// capabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
	    	try {
				driver = new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), capabilities);
			} catch (MalformedURLException badUrlException) {
				throw new TestException("Bad Url for the RemoteWebDriver's Selenium-Grid hub.");
			}
    	}
		else if (configurator.getDestination() == DESTINATION.REMOTE && configurator.getBrowser() == BROWSER.CHROME)
		{
			logger.info("Setting WebDriver to run CHROME on a REMOTEHOST.");
			throw new TestException("We're not using ChromeDriver at this time.  We saw errors using ChromeDriver and noticed Selenium defects were filed against it.");
		}  	
		else if (configurator.getDestination() == DESTINATION.REMOTE && configurator.getBrowser() == BROWSER.SAFARI)
		{
			logger.info("Setting WebDriver to run SAFARI on a REMOTEHOST.");
			throw new TestException("SAFARI not supported yet by WebDriver.");
		}
		else if (configurator.getDestination() == DESTINATION.REMOTE && configurator.getBrowser() == BROWSER.HTMLUNIT)
		{
			logger.info("Setting WebDriver to run HTMLUNIT on a REMOTEHOST.");
			throw new TestException("Sorry Charlie! I'm not supporting HtmlUnit for remote execution at this time.");
		}
    	// *** these now process running on LOCALHOST ****
    	else if (configurator.getBrowser() == BROWSER.FIREFOX) {
    		
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
    	else if (configurator.getBrowser() == BROWSER.IE) {
    		logger.info("Setting WebDriver to INTERNET EXPLORER on LOCALHOST.");
    		// TODO: research this myself--why do we need DesiredCapabilityes to INTRODUCT_FLAKINESS... with the IE driver.  I'd like to get the history behind this fix.
//    		// Introduce a security override, setting all security zones to low, so that we can actually drive IE.  
//            // This is required for IE7 and greater or any Vista or higher windows OS
            DesiredCapabilities capabilities = DesiredCapabilities.internetExplorer();
            capabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
            driver = new InternetExplorerDriver(capabilities);
    	}
		else if (configurator.getBrowser() == BROWSER.CHROME)
		{
			logger.info("Setting WebDriver to CHROME on LOCALHOST.");
			driver = new ChromeDriver();
		}
    	else if (configurator.getBrowser() == BROWSER.HTMLUNIT)
		{
    		logger.info("Setting WebDriver to HTMLUNIT on LOCALHOST.");
			// not sure why they did it this way.
			// webDriver = new HtmlUnitDriver(DesiredCapabilities.htmlUnit());
			driver = new HtmlUnitDriver(true);
			// Paul -- experimenting with different ways of connecting to the HtmlUnit driver. 
//			DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
//			HtmlUnitDriver driver = new HtmlUnitDriver(DesiredCapabilities.firefox());
//			HtmlUnitDriver driver = new HtmlUnitDriver(BrowserVersion.FIREFOX_3_6);
//			driver.setJavascriptEnabled(true);
//			webDriver = driver;		
		}
    	else if (configurator.getBrowser() == BROWSER.SAFARI) {
    		logger.info("Setting WebDriver to SAFARI on LOCALHOST.");
    		throw new TestException("Using SAFARI on LOCALHOST.");
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
