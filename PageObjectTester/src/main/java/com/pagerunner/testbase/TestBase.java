package com.pagerunner.testbase;

import com.pagerunner.utils.Configurator;
import com.pagerunner.utils.DriverManager;
import com.pagerunner.utils.TestException;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;

import java.util.concurrent.TimeUnit;

/**
 * Base class for all Tests that run page objects generated from the Page Object Helper.  Tests derive from
 * this base class.
 * Note that these tests will be run against the test pages that are provided with this project.  The tests
 * provided are for 'known pages' that were used as 'standard test pages' for testing the Page Object Helper.  Tests themselves cannot
 *
 * The base class is responsible for starting up the browser (or headless browser in the case of HTML-Unit) and to return the WebDriver.
 * It's also responsible for closing the browser.
 *
 * @author : Paul Grandjean
 * Date : 4/14/13
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





