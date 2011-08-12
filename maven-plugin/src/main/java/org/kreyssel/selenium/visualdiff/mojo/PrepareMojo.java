package org.kreyssel.selenium.visualdiff.mojo;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.kreyssel.selenium.visualdiff.core.ScreenshotManager;

/**
 * PrepareMojo.
 * 
 * @goal prepare
 * @phase validate
 */
public class PrepareMojo extends BaseMojo {

	public void execute() throws MojoExecutionException, MojoFailureException {

		if (mavenProject == null) {
			throw new MojoFailureException("project is null!");
		}

		String testOutputDir = mavenProject.getBuild().getTestOutputDirectory();

		File file = new File(testOutputDir,
				ScreenshotManager.getPropertiesFilePath());

		try {
			save(file, outputDirectory);
		} catch (IOException ex) {
			throw new MojoExecutionException(
					"Error on store screenshot path properties file!", ex);
		}
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param propertiesFile
	 *            DOCUMENT ME!
	 * @param screenshotDir
	 *            DOCUMENT ME!
	 * 
	 * @throws IOException
	 */
	private void save(final File propertiesFile, final File screenshotDir)
			throws IOException {

		propertiesFile.getParentFile().mkdirs();

		Properties props = new Properties();
		props.setProperty(ScreenshotManager.PROPERTY_OUTPUT_FILEPATH,
				screenshotDir.getAbsolutePath());

		FileWriter w = new FileWriter(propertiesFile);

		try {
			props.store(w, "stored by " + PrepareMojo.class.getName());
		} finally {
			w.close();
		}
	}
}
