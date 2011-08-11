package org.kreyssel.selenium.visualdiff.core;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.util.Properties;


/**
 * ScreenshotManager.
 */
public final class ScreenshotManager {

    public static final String PROPERTIES_FILE = "screenshotmanager.properties";

    public static final String PROPERTY_OUTPUT_PATH = "outputpath";

    /**
     * Creates a new ScreenshotManager object.
     */
    private ScreenshotManager() {

    }

    /**
     * DOCUMENT ME!
     *
     * @param   testClass       DOCUMENT ME!
     * @param   testMethodName  DOCUMENT ME!
     * @param   screenshotId    DOCUMENT ME!
     * @param   driver          DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  IOException  DOCUMENT ME!
     */
    public static File takeScreenshot( final Class<?> testClass, final String testMethodName, final String screenshotId,
        final TakesScreenshot driver ) throws IOException {

        String screenshotSignature = testClass.getName() + "_" + testMethodName + "_" + screenshotId;

        File tmpPngFile = driver.getScreenshotAs( OutputType.FILE );

        if( tmpPngFile == null ) {
            throw new RuntimeException( "Got no screenshot for test '" + screenshotSignature + "'!" );
        } else if( tmpPngFile.length() < 1 ) {
            throw new RuntimeException( "Screenshot for test '" + screenshotSignature + "' is 0 byte!" );
        }

        File targetPngFile = getScreenshotOutputPath( testClass, screenshotSignature );

        copyScreenshot( tmpPngFile, targetPngFile );

        return targetPngFile;
    }

    /**
     * saveScreenshot.
     *
     * @param   src     DOCUMENT ME!
     * @param   target  DOCUMENT ME!
     *
     * @throws  IOException
     */
    static void copyScreenshot( final File src, final File target ) throws IOException {
        System.out.println( "Copy screenshot to '" + target.getAbsolutePath() + "' (" +
            FileUtils.byteCountToDisplaySize( src.length() ) + ") ..." );

        FileUtils.copyFile( src, target );
    }

    /**
     * DOCUMENT ME!
     *
     * @param   testClass            classLoader DOCUMENT ME!
     * @param   screenshotSignature  screenshotId DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  IOException  DOCUMENT ME!
     */
    static File getScreenshotOutputPath( final Class<?> testClass, final String screenshotSignature )
        throws IOException {
        File baseOutputDir = getScreenshotOutputPath( testClass );
        String filepath = screenshotSignature.replace( '.', '/' ) + ".png";
        File targetFile = new File( baseOutputDir, filepath );

        return targetFile;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   testClass  classLoader testClass DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  IOException  DOCUMENT ME!
     */
    private static File getScreenshotOutputPath( final Class<?> testClass ) throws IOException {
        Properties props = new Properties();
        InputStream in = null;

        try {
            String filepath = getPropertiesFilePath();

            in = testClass.getResourceAsStream( filepath );

            if( in == null ) {
                throw new IOException( "Could not found '" + filepath + "'!" );
            }

            props.load( in );
        } finally {
            IOUtils.closeQuietly( in );
        }

        String outputPath = props.getProperty( PROPERTY_OUTPUT_PATH );

        if( StringUtils.isBlank( outputPath ) ) {
            throw new RuntimeException( "Could not found value for property '" + PROPERTY_OUTPUT_PATH + "' in file '" +
                PROPERTIES_FILE + "'!" );
        }

        return new File( outputPath );
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String getPropertiesFilePath() {
        String classPath = ScreenshotManager.class.getPackage().getName().replace( '.', '/' );

        String filepath = "/" + classPath + "/" + PROPERTIES_FILE;

        return filepath;
    }
}
