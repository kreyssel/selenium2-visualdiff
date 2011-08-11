package org.kreyssel.selenium.visualdiff.core.junit4;

import org.junit.rules.MethodRule;

import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

import org.kreyssel.selenium.visualdiff.core.ScreenshotManager;

import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;

import java.util.HashSet;
import java.util.Set;


/**
 * TakesScreenshotRule.
 */
public class TakesScreenshotRule implements MethodRule {

    private ScreenshotManager screenshotManager;

    /**
     * DOCUMENT ME!
     *
     * @param   base    DOCUMENT ME!
     * @param   method  DOCUMENT ME!
     * @param   target  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Statement apply( final Statement base, final FrameworkMethod method, final Object target ) {

        this.screenshotManager=new ScreenshotManager(target.getClass(), method.getName());

        return base;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   driver  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  IOException
     */
    public File takeScreenshot( final WebDriver driver ) throws IOException {
        return takeScreenshot( "1", driver );
    }

    /**
     * DOCUMENT ME!
     *
     * @param   id      DOCUMENT ME!
     * @param   driver  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  IOException
     */
    public File takeScreenshot( final String id, final WebDriver driver ) throws IOException {
        return screenshotManager.takeScreenshot( id, driver );
    }
}
