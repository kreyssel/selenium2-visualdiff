package org.kreyssel.selenium.visualdiff.mojo;

import java.io.File;
import java.util.List;
import java.util.Locale;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.metadata.ArtifactMetadataRetrievalException;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.doxia.siterenderer.Renderer;
import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.AbstractMavenReport;
import org.apache.maven.reporting.MavenReportException;
import org.codehaus.mojo.versions.api.ArtifactVersions;
import org.codehaus.mojo.versions.ordering.VersionComparators;

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
	 * The artifact metadata source to use.
	 * 
	 * @component
	 * @required
	 * @readonly
	 */
	private ArtifactMetadataSource artifactMetadataSource;
	/**
	 * @parameter expression="${project.remoteArtifactRepositories}"
	 * @readonly
	 */
	protected List remoteArtifactRepositories;

	/**
	 * @parameter expression="${localRepository}"
	 * @readonly
	 */
	protected ArtifactRepository localRepository;
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

		Artifact artifact = project.getArtifact();

		ArtifactVersions artifactVersions;
		try {
			artifactVersions = new ArtifactVersions(artifact,
					artifactMetadataSource.retrieveAvailableVersions(artifact, localRepository,
							remoteArtifactRepositories),
					VersionComparators.getVersionComparator("maven"));
		} catch (ArtifactMetadataRetrievalException ex) {
			throw new MavenReportException("Could not resolve previous versions of artifact '"
					+ artifact + "' from repositories!");
		}

	}
}
