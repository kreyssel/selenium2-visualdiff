package org.kreyssel.selenium.visualdiff.it;

import org.apache.commons.lang3.SystemUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.kreyssel.selenium.visualdiff.core.junit4.TakesScreenshotRule;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

/**
 * SimpleSeleniumIT.
 */
public class SimpleSeleniumIT {

	@Rule
	public TakesScreenshotRule screenshot = new TakesScreenshotRule();

	RemoteWebDriver driver;

	@Before
	public void init() {
		driver = createDriver();
	}

	@After
	public void destroy() {
		driver.close();
	}

	@Test
	public void testEqual() throws Exception {
		driver.get("http://localhost:8080/equal.jsp");

		screenshot.takeScreenshot(driver);
	}

	@Test
	public void testDifferent() throws Exception {
		driver.get("http://localhost:8080/different.jsp");

		screenshot.takeScreenshot(driver);
	}

	@Test
	public void test3() throws Exception {
		driver.get("http://localhost:8080");

		screenshot.takeScreenshot(driver);
	}

	private RemoteWebDriver createDriver() {
		if (SystemUtils.IS_OS_WINDOWS)
			return new InternetExplorerDriver();

		return new FirefoxDriver();
	}
}
