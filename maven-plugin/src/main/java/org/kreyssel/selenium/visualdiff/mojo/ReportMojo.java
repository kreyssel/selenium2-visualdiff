package org.kreyssel.selenium.visualdiff.mojo;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.metadata.ArtifactMetadataRetrievalException;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.siterenderer.Renderer;
import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.AbstractMavenReport;
import org.apache.maven.reporting.MavenReportException;
import org.codehaus.mojo.versions.api.ArtifactVersions;
import org.codehaus.mojo.versions.ordering.VersionComparators;
import org.kreyssel.selenium.visualdiff.core.ScreenshotStore;
import org.kreyssel.selenium.visualdiff.core.diff.VisualDiff;
import org.kreyssel.selenium.visualdiff.core.diff.VisualDiffMeta;
import org.kreyssel.selenium.visualdiff.core.diff.VisualDiffMetaGrouper;
import org.kreyssel.selenium.visualdiff.mojo.report.VisualDiffReportRenderer;
import org.kreyssel.selenium.visualdiff.mojo.report.VisualDiffReportUtil;
import org.kreyssel.selenium.visualdiff.mojo.report.VisualDiffTestReportRenderer;

import com.google.common.collect.ImmutableListMultimap;

/**
 * ReportMojo generates a report of visual diffs between two selenium2
 * functional tests runs.
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

	public String getDescription(final Locale arg0) {
		return "desc";
	}

	public String getName(final Locale arg0) {
		return "Selenium2 Visuall Diff";
	}

	public String getOutputName() {
		return "visualdiff";
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

		Artifact currentArtifact;
		try {
			currentArtifact = resolveScreenshotArtifact(artifact, project.getVersion());
		} catch (Exception ex) {
			throw new MavenReportException(
					"Error on resolve screenshot artifact for current project!", ex);
		}

		if (currentArtifact == null) {
			throw new MavenReportException(
					"Could not found screenshot archive! Did you ensure that you run the package goal before?");
		}

		Artifact previousArtifact;
		try {
			previousArtifact = getScreenshotsFromLatestRelease(artifact);
		} catch (Exception ex) {
			throw new MavenReportException(
					"Error on resolve screenshot artifact for latest project!", ex);
		}

		if (previousArtifact == null || previousArtifact.getFile() == null) {
			getLog().warn(
					"Could not found a previous release version of artifact '"
							+ project.getArtifact() + "'!");
			return;
		}

		ScreenshotStore currentScreenshotsStore = new ScreenshotStore(currentArtifact.getFile());
		ScreenshotStore previousScreenshotsStore = new ScreenshotStore(
				previousArtifact.getFile());
		VisualDiff vd = new VisualDiff(new File(outputDirectory, "images/visualdiff"));

		// render overview
		getLog().info("Render visual diff overview ...");

		List<VisualDiffMeta> diffs;
		try {
			diffs = vd.diff(currentScreenshotsStore, previousScreenshotsStore);
		} catch (IOException ex) {
			throw new MavenReportException("Error on diff screenshots!", ex);
		}

		new VisualDiffReportRenderer(getSink(), currentArtifact, previousArtifact, diffs)
				.render();

		// render report per testclass
		ImmutableListMultimap<String, VisualDiffMeta> groupedPerTest = VisualDiffMetaGrouper
				.byTestClass(diffs);
		for (String testClass : groupedPerTest.keySet()) {
			getLog().info("Render visual diff result for test '" + testClass + "' ...");

			try {
				Sink sinkForTestClass = getSinkFactory().createSink(new File(getOutputDirectory()),
						VisualDiffReportUtil.asFilename(testClass, ".html"));

				new VisualDiffTestReportRenderer(sinkForTestClass, testClass,
						groupedPerTest.get(testClass)).render();
			} catch (IOException ex) {
				getLog().error("Could not create visual diff report for test '" + testClass + "'!",
						ex);
			}
		}
	}

	protected Artifact getScreenshotsFromLatestRelease(final Artifact artifact)
			throws ArtifactMetadataRetrievalException, ArtifactResolutionException,
			ArtifactNotFoundException {

		// resolve previous version of screenshots.zip via maven metadata
		ArtifactVersions artifactVersions = new ArtifactVersions(artifact,
				artifactMetadataSource.retrieveAvailableVersions(artifact, localRepository,
						remoteArtifactRepositories),
				VersionComparators.getVersionComparator("maven"));
		ArtifactVersion newestArtifactVersion = artifactVersions.getNewestVersion(null, null);

		if (newestArtifactVersion == null) {
			return null;
		}

		return resolveScreenshotArtifact(artifact, newestArtifactVersion.toString());
	}

	protected Artifact resolveScreenshotArtifact(final Artifact artifact, final String version)
			throws ArtifactResolutionException, ArtifactNotFoundException {

		// resolve screenshots.zip artifact
		Artifact resolveArtifact = artifactFactory.createArtifactWithClassifier(
				artifact.getGroupId(), artifact.getArtifactId(), version, "zip", "screenshots");

		artifactResolver.resolve(resolveArtifact, remoteArtifactRepositories, localRepository);

		return resolveArtifact;
	}
}
