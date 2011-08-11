package org.kreyssel.selenium.visualdiff.mojo;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.kreyssel.selenium.visualdiff.core.ScreenshotManager;

/**
 * PrepareMojo.
 * 
 * @goal prepare
 * @phase validate
 */
public class PrepareMojo extends AbstractMojo {

	/**
	 * @parameter default-value="${project}"
	 * @required
	 * @readonly
	 */
	private MavenProject mavenProject;

	/**
	 * @parameter default-value="${project.basedir}/target/screenshots"
	 * @required
	 */
	private File outputDirectory;

	public void execute() throws MojoExecutionException, MojoFailureException {

		if (mavenProject == null) {
			throw new MojoFailureException("project is null!");
		}

		String dir = mavenProject.getBuild().getTestOutputDirectory();

		File file = new File(dir, ScreenshotManager.getPropertiesFilePath());

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
		props.setProperty(ScreenshotManager.PROPERTY_OUTPUT_PATH,
				screenshotDir.getAbsolutePath());

		FileWriter w = new FileWriter(propertiesFile);

		try {
			props.store(w, "stored by " + PrepareMojo.class.getName());
		} finally {
			w.close();
		}
	}
}
