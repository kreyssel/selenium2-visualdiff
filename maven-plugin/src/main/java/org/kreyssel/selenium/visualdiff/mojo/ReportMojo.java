package org.kreyssel.selenium.visualdiff.mojo;

import java.io.File;
import java.util.Locale;

import org.apache.maven.doxia.siterenderer.Renderer;
import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.AbstractMavenReport;
import org.apache.maven.reporting.MavenReportException;

/**
 * ReportMojo.
 * 
 * @goal visualdiff-report
 * @phase site
 */
public class ReportMojo extends AbstractMavenReport {
	/**
	 * Directory where reports will go.
	 * 
	 * @parameter expression="${project.reporting.outputDirectory}"
	 * @required
	 * @readonly
	 */
	private String outputDirectory;

	/**
	 * @parameter default-value="${project}"
	 * @required
	 * @readonly
	 */
	private MavenProject project;

	/**
	 * @component
	 * @required
	 * @readonly
	 */
	private Renderer siteRenderer;

	/**
	 * @parameter default-value=
	 *            "${project.build.directory}/${project.build.finalName}-screenshots.zip"
	 * 
	 * @required
	 */
	private File archiveFile;

	public String getDescription(final Locale arg0) {
		return "desc";
	}

	public String getName(final Locale arg0) {
		return "name";
	}

	public String getOutputName() {
		return "outputname";
	}

	@Override
	protected String getOutputDirectory() {
		return outputDirectory;
	}

	@Override
	protected MavenProject getProject() {
		return project;
	}

	@Override
	protected Renderer getSiteRenderer() {
		return siteRenderer;
	}

	@Override
	protected void executeReport(final Locale arg0) throws MavenReportException {
		System.out.println("\n\n\n\n\nreport\n\n\n\n\n" + archiveFile);
	}

}
