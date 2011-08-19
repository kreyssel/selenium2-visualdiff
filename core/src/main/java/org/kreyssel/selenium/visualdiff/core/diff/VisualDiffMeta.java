package org.kreyssel.selenium.visualdiff.core.diff;

import java.io.File;

import org.kreyssel.selenium.visualdiff.core.ScreenshotMeta;

public class VisualDiffMeta extends ScreenshotMeta {

	public final Type diffType;

	public enum Type {
		EQUAL, ADDED, REMOVED, DIFFERENT;
	}

	protected VisualDiffMeta(final ScreenshotMeta screenshotMeta, final Type diffType) {
		super(screenshotMeta);
		this.diffType = diffType;
	}

	public String getScreenshot1Filepath() {
		return getFile("New");
	}

	public String getScreenshot2Filepath() {
		return getFile("Old");
	}

	public String getScreenshotDiffFilepath() {
		return getFile("Diff");
	}

	private String getFile(final String suffix) {
		File file = new File(this.filepath);
		String name = file.getName();
		int idx = name.lastIndexOf('.');
		String newName = name.substring(0, idx) + "_" + suffix + name.substring(idx);

		return new File(file.getParent(), newName).getPath();
	}
}
