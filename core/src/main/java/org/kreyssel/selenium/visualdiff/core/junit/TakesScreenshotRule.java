package org.kreyssel.selenium.visualdiff.core.junit;

import org.junit.rules.MethodRule;

import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

import org.kreyssel.selenium.visualdiff.core.ScreenshotManager;

import org.openqa.selenium.TakesScreenshot;

import java.io.File;
import java.io.IOException;

import java.util.HashSet;
import java.util.Set;


/**
 * TakesScreenshotRule.
 */
public class TakesScreenshotRule implements MethodRule {

    private Class<?> testClass;

    private String testMethodName;

    private Set<String> screenshotIds;

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

        this.testClass = target.getClass();
        this.testMethodName = method.getName();
        this.screenshotIds = new HashSet<String>();

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
    public File takeScreenshot( final TakesScreenshot driver ) throws IOException {
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
    public File takeScreenshot( final String id, final TakesScreenshot driver ) throws IOException {
        validateScreenshotId( id );

        return ScreenshotManager.takeScreenshot( testClass, testMethodName, id, driver );
    }

    /**
     * DOCUMENT ME!
     *
     * @param  screenshotId  DOCUMENT ME!
     */
    private void validateScreenshotId( final String screenshotId ) {

        if( !screenshotId.matches( "[a-zA-Z0-9]+" ) ) {
            throw new RuntimeException( "Wrong screenshot id format '" + screenshotId + "'!" );
        }

        if( this.screenshotIds.contains( screenshotId ) ) {
            throw new RuntimeException( "Duplicate screenshot id '" + screenshotId + "'!" );
        }

        this.screenshotIds.add( screenshotId );
    }
}
