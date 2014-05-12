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

        driver = DriverManager.getDriver();
        // Set implicit waits throughout the whole test.
        driver.manage().timeouts().implicitlyWait(Configurator.DEFAULT_IMPLICIT_WAIT_TIME, TimeUnit.SECONDS);
	}

	@AfterClass
	public synchronized void tearDown() {
		logger.info("*** Tear Down ***");
		logger.info("Closing browser.");
		driver.close();
	}

}





