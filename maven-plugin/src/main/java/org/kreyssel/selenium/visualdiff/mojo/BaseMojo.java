package org.kreyssel.selenium.visualdiff.mojo;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;

/**
 * BaseMojo for all project mojos
 */
public abstract class BaseMojo extends AbstractMojo {

	/**
	 * Maven Project
	 * 
	 * @parameter default-value="${project}"
	 * @required
	 * @readonly
	 */
	MavenProject mavenProject;

	/**
	 * Maven ProjectHelper.
	 * 
	 * @component
	 * @readonly
	 */
	MavenProjectHelper projectHelper;

	/**
	 * @parameter default-value=
	 *            "${project.build.directory}/${project.build.finalName}-screenshots.zip"
	 * 
	 * @required
	 */
	File archiveFile;

}
