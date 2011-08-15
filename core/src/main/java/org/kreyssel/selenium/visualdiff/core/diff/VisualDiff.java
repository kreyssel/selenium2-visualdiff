package org.kreyssel.selenium.visualdiff.core.diff;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.kreyssel.selenium.visualdiff.core.ScreenshotMeta;
import org.kreyssel.selenium.visualdiff.core.ScreenshotStore;

public class VisualDiff {

	private ScreenshotStore store1;
	private ScreenshotStore store2;

	public VisualDiff(final ScreenshotStore store1, final ScreenshotStore store2) {
		this.store1 = store1;
		this.store2 = store2;
	}

	public void diff() throws IOException {
		List<ScreenshotMeta> screenshots1 = store1.getScreenshots();
		List<ScreenshotMeta> screenshots2 = store2.getScreenshots();
	}

	protected void diff(final List<ScreenshotMeta> screenshots1,
			final List<ScreenshotMeta> screenshots2) {

		for (ScreenshotMeta screenshot1 : screenshots1) {
			if (screenshots2.contains(screenshot1)) {
				ScreenshotMeta screenshot2 = screenshots2.get(screenshots2.indexOf(screenshot1));

				ImageCompare ic;
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

				boolean equal;
				try {
					equal = ic.compare();
				} catch (InterruptedException ex) {
					throw new IOException("Error on compare screenshots!", ex);
				}

				if (!equal) {
					File diffFile = new File(outputDirectory, currentScreenshot.path);
					diffFile.getParentFile().mkdirs();
					ic.saveDiffAsPng(diffFile);
				}
			}
		}
	}

}
