package org.kreyssel.selenium.visualdiff.mojo;

import java.io.File;
import java.util.List;
import java.util.Locale;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.metadata.ArtifactMetadataRetrievalException;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.VersionRange;
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
	 * Used to look up Artifacts in the remote repository.
	 * 
	 * @parameter expression=
	 *            "${component.org.apache.maven.artifact.factory.ArtifactFactory}"
	 * @required
	 * @readonly
	 */
	protected ArtifactFactory artifactFactory;

	/**
	 * @component
	 * @since 1.0-alpha-3
	 */
	private ArtifactResolver artifactResolver;

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

		ArtifactVersion newestArtifactVersion = artifactVersions.getNewestVersion(null, null);

		artifactFactory.createArtifact(project.getGroupId(), project.getArtifactId(), newestArtifactVersion.toString(), scope, type)
		
		Artifact previousScreenshotArtifact = new DefaultArtifact(artifact.getGroupId(),
				artifact.getArtifactId(), newestVersionRange, null, "zip", "screenshots", null);
		try {
			artifactResolver.resolve(previousScreenshotArtifact, remoteArtifactRepositories,
					localRepository);
		} catch (Exception ex) {
			throw new MavenReportException("Error on resolve previous screenshots!", ex);
		}
	}
}
