package org.kreyssel.selenium.visualdiff.core.diff;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.kreyssel.selenium.visualdiff.core.ScreenshotMeta;
import org.kreyssel.selenium.visualdiff.core.ScreenshotStore;
import org.kreyssel.selenium.visualdiff.core.diff.VisualDiffMeta.Type;

public class VisualDiff {

	private ScreenshotStore store1;
	private ScreenshotStore store2;
	private File outputDirectory;

	public VisualDiff(final ScreenshotStore store1, final ScreenshotStore store2,
			final File outputDirectory) {
		this.store1 = store1;
		this.store2 = store2;
		this.outputDirectory = outputDirectory;
	}

	public List<VisualDiffMeta> diff() throws IOException {
		List<ScreenshotMeta> screenshots1 = store1.getScreenshots();
		List<ScreenshotMeta> screenshots2 = store2.getScreenshots();

		ArrayList<VisualDiffMeta> diffs = new ArrayList<VisualDiffMeta>();
		diff(diffs, screenshots1, screenshots2, Type.ADDED);
		diff(diffs, screenshots2, screenshots1, Type.REMOVED);

		return diffs;
	}

	protected void diff(final List<VisualDiffMeta> diffMeta,
			final List<ScreenshotMeta> screenshots1, final List<ScreenshotMeta> screenshots2,
			final Type diffType) throws IOException {

		for (ScreenshotMeta screenshot1 : screenshots1) {
			if (diffMeta.contains(screenshot1)) {
				continue;
			}

			boolean different = true;

			ScreenshotMeta screenshot2 = null;
			ImageCompare ic = null;

			if (screenshots2.contains(screenshot1)) {
				screenshot2 = screenshots2.get(screenshots2.indexOf(screenshot1));

				InputStream in1 = null;
				InputStream in2 = null;

				try {
					in1 = store1.getInputStream(screenshot1.path);
					in2 = store2.getInputStream(screenshot2.path);
					ic = new ImageCompare(in1, in2);
				} finally {
					IOUtils.closeQuietly(in1);
					IOUtils.closeQuietly(in2);
				}

				try {
					different = !(ic.compare());
				} catch (InterruptedException ex) {
					throw new IOException("Error on compare screenshots!", ex);
				}
			}

			VisualDiffMeta vdMeta = new VisualDiffMeta(screenshot1, different ? Type.DIFFERENT
					: diffType);
			diffMeta.add(vdMeta);

			File sc1File = new File(outputDirectory, vdMeta.getScreenshot1Filepath());
			store1.copy(screenshot1.path, sc1File);

			if (different) {
				File sc2File = new File(outputDirectory, vdMeta.getScreenshot2Filepath());
				store2.copy(screenshot2.path, sc2File);

				File diffFile = new File(outputDirectory, vdMeta.getScreenshotDiffFilepath());
				ic.saveDiffAsPng(diffFile);
			}
		}
	}
}
