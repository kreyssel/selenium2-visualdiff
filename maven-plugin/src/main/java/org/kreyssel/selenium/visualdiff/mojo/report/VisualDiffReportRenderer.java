package org.kreyssel.selenium.visualdiff.mojo.report;

import java.util.List;

import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.SinkEventAttributeSet;
import org.apache.maven.doxia.sink.SinkEventAttributes;
import org.apache.maven.reporting.AbstractMavenReportRenderer;
import org.kreyssel.selenium.visualdiff.core.diff.VisualDiffMeta;
import org.kreyssel.selenium.visualdiff.core.diff.VisualDiffMeta.Type;

public class VisualDiffReportRenderer extends AbstractMavenReportRenderer {

	final List<VisualDiffMeta> diffs;

	public VisualDiffReportRenderer(final Sink sink, final List<VisualDiffMeta> diffs) {
		super(sink);
		this.diffs = diffs;
	}

	@Override
	public String getTitle() {
		return "Selenium2 VisualDiff";
	}

	@Override
	protected void renderBody() {

		for (VisualDiffMeta diff : diffs) {
			String testDetails = "Test-Class: " + diff.testClass + ", Method: " + diff.testMethod
					+ ", ID: " + diff.screenshotId;
			String diffTypeDetails = "Screenshot is different: " + diff.diffType;

			String sc1ImgPath = "visualdiff/" + diff.getScreenshot1Filepath();
			String sc2ImgPath = "visualdiff/" + diff.getScreenshot2Filepath();
			String diffImgPath = "visualdiff/" + diff.getScreenshotDiffFilepath();

			sink.section1();
			sink.sectionTitle1();
			sink.text(testDetails);
			sink.sectionTitle1_();

			sink.paragraph();
			sink.text(diffTypeDetails);
			sink.paragraph_();

			sink.paragraph();

			if (diff.diffType != Type.REMOVED) {
				addScreenshot(sc1ImgPath);
			}

			if (diff.diffType == Type.DIFFERENT) {
				addScreenshot(diffImgPath);
			}

			if (diff.diffType == Type.DIFFERENT || diff.diffType == Type.REMOVED) {
				addScreenshot(sc2ImgPath);
			}

			sink.paragraph_();

			sink.section1_();
		}
	}

	private void addScreenshot(final String path) {
		SinkEventAttributeSet smallImagesAttr = new SinkEventAttributeSet();
		smallImagesAttr.addAttribute(SinkEventAttributes.HEIGHT, "200");
		smallImagesAttr.addAttribute(SinkEventAttributes.BORDER, "1");

		sink.link(path);
		sink.figureGraphics(path, smallImagesAttr);
		sink.link_();
	}
}
