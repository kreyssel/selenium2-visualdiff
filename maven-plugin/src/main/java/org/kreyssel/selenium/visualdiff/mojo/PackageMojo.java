package org.kreyssel.selenium.visualdiff.mojo;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * PackageMojo.
 * 
 * @goal package
 * @phase post-integration-test
 */
public class PackageMojo extends BaseMojo {

	public void execute() throws MojoExecutionException, MojoFailureException {

		// attach screenshots zip to store in repository
		projectHelper.attachArtifact(mavenProject, "zip", "screenshots", archiveFile);

		getLog().info("Screenshots archive stored as " + archiveFile.getAbsolutePath());
	}
}
