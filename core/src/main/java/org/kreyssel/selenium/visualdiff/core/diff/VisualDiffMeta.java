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
		return getFile("ScrShot1");
	}

	public String getScreenshot2Filepath() {
		return getFile("ScrShot2");
	}

	public String getScreenshotDiffFilepath() {
		return getFile("Diff");
	}

	private String getFile(final String suffix) {
		File filepath = new File(path);
		String name = filepath.getName();
		int idx = name.lastIndexOf('.');
		String newName = name.substring(0, idx) + "_" + suffix + name.substring(idx);

		return new File(filepath.getParent(), newName).getPath();
	}
}
