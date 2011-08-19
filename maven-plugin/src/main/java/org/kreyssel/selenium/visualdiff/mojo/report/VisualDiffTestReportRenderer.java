package org.kreyssel.selenium.visualdiff.mojo.report;

import java.util.List;

import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.SinkEventAttributeSet;
import org.apache.maven.doxia.sink.SinkEventAttributes;
import org.apache.maven.reporting.AbstractMavenReportRenderer;
import org.kreyssel.selenium.visualdiff.core.diff.VisualDiffMeta;
import org.kreyssel.selenium.visualdiff.core.diff.VisualDiffMeta.Type;
import org.kreyssel.selenium.visualdiff.core.diff.VisualDiffMetaGrouper;
import org.kreyssel.selenium.visualdiff.mojo.report.VisualDiffReportUtil.Depth;

import com.google.common.collect.ImmutableListMultimap;

public class VisualDiffTestReportRenderer extends AbstractMavenReportRenderer {

	final String testClass;
	final List<VisualDiffMeta> diffs;

	public VisualDiffTestReportRenderer(final Sink sink, final String testClass,
			final List<VisualDiffMeta> diffs) {
		super(sink);
		this.testClass = testClass;
		this.diffs = diffs;
	}

	@Override
	public String getTitle() {
		return "Selenium2 VisualDiff for Test-Class " + testClass;
	}

	@Override
	protected void renderBody() {
		sink.paragraph();
		sink.link("visualdiff.html");
		sink.text("< back to overview");
		sink.link_();
		sink.paragraph_();

		ImmutableListMultimap<String, VisualDiffMeta> perMethod = VisualDiffMetaGrouper
				.byTestMethod(diffs);
		VisualDiffReportUtil.renderTable(sink, Depth.METHOD, "Result for Test " + testClass,
				perMethod);

		for (String testMethod : perMethod.keySet()) {
			sink.section3();
			sink.sectionTitle3();
			sink.text("Test Method " + testMethod);
			sink.sectionTitle3_();
			sink.section3_();

			for (VisualDiffMeta diffMeta : perMethod.get(testMethod)) {
				sink.section4();
				sink.sectionTitle4();
				sink.text("Screenshot ID " + diffMeta.screenshotId);
				sink.sectionTitle4_();

				if (diffMeta.diffType != Type.REMOVED)
					addScreenshot("images/visualdiff/" + diffMeta.getScreenshot1Filepath());

				if (diffMeta.diffType == Type.DIFFERENT)
					addScreenshot("images/visualdiff/" + diffMeta.getScreenshotDiffFilepath());

				if (diffMeta.diffType == Type.DIFFERENT || diffMeta.diffType == Type.REMOVED)
					addScreenshot("images/visualdiff/" + diffMeta.getScreenshot2Filepath());

				sink.section4_();
			}

			sink.link(VisualDiffReportUtil.asFilename(diffs.get(0).testClass, ".html"));
			sink.text("^ top");
			sink.link_();
		}
	}

	private void addScreenshot(final String path) {
		SinkEventAttributeSet smallImagesAttr = new SinkEventAttributeSet();
		smallImagesAttr.addAttribute(SinkEventAttributes.HEIGHT, "200");
		smallImagesAttr.addAttribute(SinkEventAttributes.HSPACE, "20");
		smallImagesAttr.addAttribute(SinkEventAttributes.BORDER, "1");

		sink.link(path);
		sink.figureGraphics(path, smallImagesAttr);
		sink.link_();
	}
}
