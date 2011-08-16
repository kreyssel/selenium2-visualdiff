package org.kreyssel.selenium.visualdiff.core;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import de.schlichtherle.truezip.file.TFile;
import de.schlichtherle.truezip.file.TFileInputStream;
import de.schlichtherle.truezip.file.TFileOutputStream;

public class ScreenshotStore {

	private TFile archive;

	public ScreenshotStore(final File storeFile) {
		this.archive = new TFile(storeFile);
	}

	public ScreenshotMeta addScreenshot(final String testClass, final String testMethod,
			final String screenshotId, final String url, final String title, final File file)
			throws IOException {
		String pngPath = getPath(testClass, testMethod, screenshotId, ".png");
		String propPath = getPath(testClass, testMethod, screenshotId, ".properties");

		TFile fileInArchive = new TFile(this.archive, pngPath);

		// copy screenshot file to archive
		new TFile(file).cp(fileInArchive);

		ScreenshotMeta meta = new ScreenshotMeta(testClass, testMethod, screenshotId, url, title,
				fileInArchive.getInnerEntryName());

		TFileOutputStream out = new TFileOutputStream(new TFile(this.archive, propPath));
		try {
			meta.store(out);
		} finally {
			out.close();
		}

		return meta;
	}

	public List<ScreenshotMeta> getScreenshots() throws IOException {
		ArrayList<ScreenshotMeta> metaList = new ArrayList<ScreenshotMeta>();

		readMeta(metaList, archive.listFiles(new PropertiesFileFilter()));

		return metaList;
	}

	public InputStream getInputStream(final String filepath) throws IOException {
		return new TFileInputStream(new TFile(archive, filepath));
	}

	public void copy(final String filepath, final File file) throws IOException {
		file.getParentFile().mkdirs();
		new TFile(archive, filepath).cp(file);
	}

	protected void readMeta(final ArrayList<ScreenshotMeta> metaList, final TFile[] entries)
			throws IOException {
		for (TFile file : entries) {
			if (file.isDirectory()) {
				readMeta(metaList, file.listFiles());
			} else if (file.getName().endsWith(".properties")) {
				metaList.add(loadMeta(file));
			}
		}
	}

	protected ScreenshotMeta loadMeta(final TFile file) throws IOException {
		ScreenshotMeta meta;

		TFileInputStream in = new TFileInputStream(file);
		try {
			meta = ScreenshotMeta.load(in);
		} finally {
			in.close();
		}

		return meta;
	}

	protected String getPath(final String testClass, final String testMethod,
			final String screenshotId, final String fileEnding) {
		return (testClass + "_" + testMethod + "_" + screenshotId).replace('.', '/') + fileEnding;
	}

	/**
	 * class PropertiesFileFilter
	 * 
	 * @author kreyssel
	 */
	private static class PropertiesFileFilter implements FileFilter {

		public boolean accept(final File file) {
			return file.isDirectory() || file.getName().endsWith(".properties");
		}
	}
}
