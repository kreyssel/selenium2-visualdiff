package org.kreyssel.selenium.visualdiff.mojo;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
import org.apache.maven.doxia.siterenderer.Renderer;
import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.AbstractMavenReport;
import org.apache.maven.reporting.MavenReportException;
import org.codehaus.mojo.versions.api.ArtifactVersions;
import org.codehaus.mojo.versions.ordering.VersionComparators;
import org.kreyssel.selenium.visualdiff.core.ScreenshotMeta;
import org.kreyssel.selenium.visualdiff.core.ScreenshotStore;
import org.kreyssel.selenium.visualdiff.core.images.ImageCompare;
import org.kreyssel.selenium.visualdiff.mojo.report.VisualDiffReportRenderer;

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
	 * @parameter expression="${project.reporting.outputDirectory}/visualdiff"
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

		File currentArchiveFile;
		try {
			currentArchiveFile = resolveScreenshotArtifact(artifact, project.getArtifact()
					.getVersion());
		} catch (Exception ex) {
			throw new MavenReportException(
					"Error on resolve screenshot artifact for current project!", ex);
		}

		if (currentArchiveFile == null) {
			throw new MavenReportException(
					"Could not found screenshot archive! Did you ensure that you run the package goal before?");
		}

		File previousArchiveFile;
		try {
			previousArchiveFile = getScreenshotsFromLatestRelease(artifact);
		} catch (Exception ex) {
			throw new MavenReportException(
					"Error on resolve screenshot artifact for latest project!", ex);
		}

		if (previousArchiveFile == null) {
			getLog().warn(
					"Could not found a previous release version of artifact '"
							+ project.getArtifact() + "'!");
			return;
		}

		ScreenshotStore currentScreenshotsStore = new ScreenshotStore(currentArchiveFile);
		List<ScreenshotMeta> currentScreenshots;
		try {
			currentScreenshots = currentScreenshotsStore.getScreenshots();
		} catch (IOException ex) {
			throw new MavenReportException("Error on retrieving current screenshots list!", ex);
		}

		ScreenshotStore previousScreenshotsStore = new ScreenshotStore(previousArchiveFile);
		List<ScreenshotMeta> lastScreenshots;
		try {
			lastScreenshots = previousScreenshotsStore.getScreenshots();
		} catch (IOException ex) {
			throw new MavenReportException(
					"Error on retrieving previous version screenshots list!", ex);
		}

		for (ScreenshotMeta currentScreenshot : currentScreenshots) {
			if (lastScreenshots.contains(currentScreenshot)) {
				try {
					ScreenshotMeta previousScreenshot = lastScreenshots.get(lastScreenshots
							.indexOf(currentScreenshot));

					InputStream inCurr = currentScreenshotsStore
							.getInputStream(currentScreenshot.path);
					InputStream inLast = previousScreenshotsStore
							.getInputStream(previousScreenshot.path);

					ImageCompare ic = new ImageCompare(inCurr, inLast);

					inCurr.close();
					inLast.close();

					boolean equal;
					try {
						equal = ic.compare();
					} catch (InterruptedException ex) {
						throw new MavenReportException("Error on compare screenshots!", ex);
					}

					if (!equal) {
						File diffFile = new File(outputDirectory, currentScreenshot.path);
						getLog().info(
								"Detect a different screenshot and stored diff image in '"
										+ diffFile.getAbsolutePath() + "'!");
						diffFile.getParentFile().mkdirs();
						ic.saveDiffAsPng(diffFile);
					}
				} catch (IOException ex) {
					getLog().warn("Error on compare images!", ex);
				}
			}
		}

		for (ScreenshotMeta screenshot : lastScreenshots) {
			System.out.println(screenshot);
		}

		new VisualDiffReportRenderer(getSink()).render();
	}

	protected File getScreenshotsFromLatestRelease(final Artifact artifact)
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

	protected File resolveScreenshotArtifact(final Artifact artifact, final String version)
			throws ArtifactResolutionException, ArtifactNotFoundException {

		// resolve screenshots.zip artifact
		Artifact resolveArtifact = artifactFactory.createArtifactWithClassifier(
				artifact.getGroupId(), artifact.getArtifactId(), version, "zip", "screenshots");

		artifactResolver.resolve(resolveArtifact, remoteArtifactRepositories, localRepository);

		return resolveArtifact.getFile();
	}
}
