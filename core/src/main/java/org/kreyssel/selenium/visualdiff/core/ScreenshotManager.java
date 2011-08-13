package org.kreyssel.selenium.visualdiff.core;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import de.schlichtherle.truezip.file.TFile;

/**
 * ScreenshotManager.
 */
public final class ScreenshotManager {

	public static final String PROPERTIES_FILE = "screenshotmanager.properties";

	public static final String PROPERTY_OUTPUT_FILEPATH = "outputpath";

	private final File archiveFile;

	private final Class<?> testClass;

	private final String testMethodName;

	private final Set<String> screenshotIds;

	/**
	 * Creates a new ScreenshotManager object.
	 * 
	 * @throws IOException
	 */
	public ScreenshotManager(final Class<?> testClass, final String testMethodName) {
		try {
			this.archiveFile = getScreenshotArchivePath(testClass);
		} catch (IOException ex) {
			throw new RuntimeException("Error on get archive filepath!", ex);
		}
		this.testClass = testClass;
		this.testMethodName = testMethodName;
		this.screenshotIds = new HashSet<String>();
	}

	/**
	 * Take screenshot.
	 * 
	 * @param screenshotId
	 *            the screenshot id
	 * @param driver
	 *            the driver
	 * @return the file
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public TFile takeScreenshot(final WebDriver driver, final String screenshotId)
			throws IOException {

		validateScreenshotId(screenshotId);

		String screenshotSignature = testClass.getName() + "_" + testMethodName + "_"
				+ screenshotId;

		if (!(driver instanceof TakesScreenshot)) {
			throw new RuntimeException("Class '" + driver.getClass().getName()
					+ "' is not a instance of '" + TakesScreenshot.class.getName() + "'!");
		}

		TakesScreenshot screenshotDriver = (TakesScreenshot) driver;

		File tmpPngFile = screenshotDriver.getScreenshotAs(OutputType.FILE);

		if (tmpPngFile == null) {
			throw new RuntimeException("Got no screenshot for test '" + screenshotSignature + "'!");
		} else if (tmpPngFile.length() < 1) {
			throw new RuntimeException("Screenshot for test '" + screenshotSignature
					+ "' is 0 byte!");
		}

		String screenshotFilepath = getScreenshotArchivePath(screenshotSignature);

		TFile fileInArchive = copyScreenshot(tmpPngFile, archiveFile, screenshotFilepath);

		return fileInArchive;
	}

	/**
	 * saveScreenshot.
	 * 
	 * @param src
	 *            DOCUMENT ME!
	 * @param target
	 *            DOCUMENT ME!
	 * 
	 * @throws IOException
	 */
	TFile copyScreenshot(final File src, final File archive, final String filepathInArchive)
			throws IOException {
		System.out.println("Copy screenshot to '" + filepathInArchive + "' ("
				+ FileUtils.byteCountToDisplaySize(src.length()) + ") ...");

		return new TFile(src).cp(new TFile(archive, filepathInArchive));
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param testClass
	 *            classLoader DOCUMENT ME!
	 * @param screenshotSignature
	 *            screenshotId DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 * 
	 * @throws IOException
	 *             DOCUMENT ME!
	 */
	String getScreenshotArchivePath(final String screenshotSignature) throws IOException {
		String filepath = screenshotSignature.replace('.', '/') + ".png";

		return filepath;
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param testClass
	 *            classLoader testClass DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 * 
	 * @throws IOException
	 *             DOCUMENT ME!
	 */
	private File getScreenshotArchivePath(final Class<?> locatorClass) throws IOException {
		Properties props = new Properties();
		InputStream in = null;

		try {
			String filepath = getPropertiesFilePath();

			in = locatorClass.getResourceAsStream(filepath);

			if (in == null) {
				throw new IOException("Could not found '" + filepath + "'!");
			}

			props.load(in);
		} finally {
			IOUtils.closeQuietly(in);
		}

		String outputPath = props.getProperty(PROPERTY_OUTPUT_FILEPATH);

		if (StringUtils.isBlank(outputPath)) {
			throw new RuntimeException("Could not found value for property '"
					+ PROPERTY_OUTPUT_FILEPATH + "' in file '" + PROPERTIES_FILE + "'!");
		}

		return new File(outputPath);
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public static String getPropertiesFilePath() {
		String classPath = ScreenshotManager.class.getPackage().getName().replace('.', '/');

		String filepath = "/" + classPath + "/" + PROPERTIES_FILE;

		return filepath;
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param screenshotId
	 *            DOCUMENT ME!
	 */
	void validateScreenshotId(final String screenshotId) {

		if (!screenshotId.matches("[a-zA-Z0-9]+")) {
			throw new RuntimeException("Wrong screenshot id format '" + screenshotId + "'!");
		}

		if (this.screenshotIds.contains(screenshotId)) {
			throw new RuntimeException("Duplicate screenshot id '" + screenshotId + "'!");
		}

		this.screenshotIds.add(screenshotId);
	}
}
