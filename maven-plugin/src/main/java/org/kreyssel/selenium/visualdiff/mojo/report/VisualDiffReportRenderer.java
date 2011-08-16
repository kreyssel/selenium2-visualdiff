package org.kreyssel.selenium.visualdiff.mojo.report;

import java.util.List;

import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.reporting.AbstractMavenReportRenderer;
import org.kreyssel.selenium.visualdiff.core.diff.VisualDiffMeta;

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
			sink.section1();
			sink.sectionTitle1();
			sink.text("Test-Class: " + diff.testClass + ", Method: " + diff.testMethod + ", ID: "
					+ diff.screenshotId);
			sink.sectionTitle1_();

			sink.text("Screenshot is different: " + diff.diffType);
			sink.section1_();
		}
	}

}
