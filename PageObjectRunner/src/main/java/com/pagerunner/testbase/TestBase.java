package com.pagerunner.testbase;

import java.util.concurrent.TimeUnit;

import com.pagerunner.utils.DriverManager;
import com.pagerunner.utils.TestException;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.openqa.selenium.WebDriver;

import com.pagerunner.utils.Configurator;

import org.openqa.selenium.WebDriverException;
import org.testng.*;
import org.testng.annotations.*;

/**
 * User: pgrandje  TODO:  javadocs for TestBase
 * Date: 4/14/13
 * Time: 1:40 PM
 * To change this template use File | Settings | File Templates.
 * The base class is responsible for starting up the browser (or headless browser in the case of HTML-Unit) and to return the WebDriver.
 * It's also responsible for closing the browser.
 */
public class TestBase {

    protected Logger logger;
	protected Configurator configurator;
    protected WebDriver driver;

    /* Configurator needs to be explicitly assigned before the DriverManager runs.  Therefore a constructor makes the most
       sense to ensure that everywhere the test class exists, a configurator will be available.
    */
    public TestBase() {
        PropertyConfigurator.configure("log4j.properties");
        logger = Logger.getLogger(this.getClass());
        configurator = Configurator.get();
    }


	@BeforeClass
    @Parameters("url")
	public void startUp(String url) throws TestException {

        logger.info("*** Start Up ***");
        configurator.setUrl(url);

		try {

			driver = DriverManager.getDriver();
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


	@AfterClass
	public synchronized void tearDown() {
		logger.info("*** Tear Down ***");
		logger.info("Closing browser.");
		driver.close();
	}


    // TODO: Do I need this method: getPageObjectAndLogIt()?
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





