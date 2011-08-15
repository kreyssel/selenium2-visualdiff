package org.kreyssel.selenium.visualdiff.mojo.report;

import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.reporting.AbstractMavenReportRenderer;

public class VisualDiffReportRenderer extends AbstractMavenReportRenderer {

	public VisualDiffReportRenderer(final Sink sink) {
		super(sink);
	}

	@Override
	public String getTitle() {
		return "Selenium2 VisualDiff";
	}

	@Override
	protected void renderBody() {
		sink.sectionTitle1();
		sink.text("xxx");
	}

}
