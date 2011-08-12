package org.kreyssel.selenium.visualdiff.mojo;

import java.io.File;
import java.io.IOException;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import de.schlichtherle.truezip.file.TFile;

/**
 * PackageMojo.
 * 
 * @goal package
 * @phase post-integration-test
 */
public class PackageMojo extends BaseMojo {

	public void execute() throws MojoExecutionException, MojoFailureException {
		File destDir = new File(mavenProject.getBuild().getDirectory());
		File destFile = new File(destDir, mavenProject.getBuild()
				.getFinalName() + "-screenshots.zip");

		try {
			new TFile(outputDirectory).cp_rp(destFile);
		} catch (IOException ex) {
			throw new MojoExecutionException(
					"Error on zipping screenshots to file '"
							+ destFile.getAbsolutePath() + "'!", ex);
		}

		// attach screenshots zip to store in repository
		projectHelper.attachArtifact(mavenProject, "zip", "screenshots",
				destFile);

		getLog().info(
				"Screenshots archive stored as " + destFile.getAbsolutePath());
	}
}
