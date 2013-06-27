package com.selgentests.testbase;

import java.util.concurrent.TimeUnit;

import com.selgentests.utils.DriverManager;
import com.selgentests.utils.TestException;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.openqa.selenium.WebDriver;

import com.selgentests.utils.Configurator;

import org.openqa.selenium.WebDriverException;
import org.testng.*;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

/**
 * Created by IntelliJ IDEA.
 * User: pgrandje
 * Date: 4/14/13
 * Time: 1:40 PM
 * To change this template use File | Settings | File Templates.
 */
/* The base class is responsible for starting up the browser (or headless browser in the case of HTML-Unit) and to return the WebDriver.
 * It's also responsible for closing the browser.
 */
public class TestBase {

    protected Logger logger;

	protected WebDriver driver;

	protected Configurator testConfigurator;


	@BeforeMethod
	public void startUp() throws TestException {

        // Used by the loggers
//        PropertyConfigurator.configure("log4j.properties");

		logger = Logger.getLogger(this.getClass());
		logger.info("*** Running startUp ***");

		// Retrieves command-line args and stored the configuration values.  These values are then retrieved
		//		for setting up the WebDriver and for login.
		testConfigurator = Configurator.get();


		try {

			driver = DriverManager.getDriver(testConfigurator);
			Assert.assertNotNull(driver, "*** TEST FAILURE -- WEBDRIVER IS NULL!!! ***");

			// Set implicit waits throughout the whole test.
			driver.manage().timeouts().implicitlyWait(Configurator.DEFAULT_IMPLICIT_WAIT_TIME, TimeUnit.SECONDS);

		} catch (TestException testException) {
			logger.error("TEST FAILURE -- WEBDRIVER SETUP FAILED, BROWSER NOT OPENED -- TestBase caught TestException -- Exception Message: " + testException.getMessage());
			logger.error("Exception Cause: " + testException.getCause());
			logger.error("Exception Class: " + testException.getClass());
		} catch (WebDriverException webDriverException) {
            System.out.println("TEST FAILURE -- WEBDRIVER SETUP FAILED, BROWSER NOT OPENED -- TestBase caught an arbitrary Exception -- Exception Message " + webDriverException.getMessage());
			logger.error("TEST FAILURE -- WEBDRIVER SETUP FAILED, BROWSER NOT OPENED -- TestBase caught  RuntimeException -- Exception Message " + webDriverException.getMessage());
			logger.error("Exception Cause: " + webDriverException.getCause());
			logger.error("Exception Class: " + webDriverException.getClass());
		}
		catch (Exception exception) {
            System.out.println("TEST FAILURE -- WEBDRIVER SETUP FAILED, BROWSER NOT OPENED -- TestBase caught an arbitrary Exception -- Exception Message " + exception.getMessage());
			logger.error("TEST FAILURE -- WEBDRIVER SETUP FAILED, BROWSER NOT OPENED -- TestBase caught an arbitrary Exception -- Exception Message " + exception.getMessage());
			logger.error("Exception Cause: " + exception.getCause());
			logger.error("Exception Class: " + exception.getClass());
		}

	}


	@AfterMethod
	public synchronized void tearDown() {
		logger.info("*** Running tearDown ***");
		logger.info("Closing browser.");
		driver.close();
	}



	// Gets the Header page object and checks that it's ready for testing.
	// Logs it's progress using INFO logging level.
//	protected <T> PageObjectBase getPageObjectAndLogIt(Class<T> pageObjectClassName) {
//
//		logger.info("Fetching page object " + pageObjectClassName.getName() + " in TestBase.");
//		MtmPageObjectBase pageObject = (MtmPageObjectBase) PageFactory.initElements(driver, pageObjectClassName);
//		logger.info(pageObjectClassName.getName() + " page object is ready.");
//
//		return pageObject;
//	}

}





