package org.kreyssel.selenium.visualdiff.it;

import org.junit.Rule;
import org.junit.Test;

import org.kreyssel.selenium.visualdiff.core.junit.TakesScreenshotRule;

import org.openqa.selenium.ie.InternetExplorerDriver;


/**
 * SimpleSeleniumIT.
 */
public class SimpleSeleniumIT {

    @Rule
    public TakesScreenshotRule screenshot = new TakesScreenshotRule();

    @Test
    public void test1() throws Exception {
        InternetExplorerDriver driver = new InternetExplorerDriver();

        driver.navigate().to( "http://localhost:8080" );

        screenshot.takeScreenshot( driver );
    }

    @Test
    public void test2() throws Exception {
        InternetExplorerDriver driver = new InternetExplorerDriver();

        driver.navigate().to( "http://localhost:8080" );

        screenshot.takeScreenshot( driver );
    }
}
