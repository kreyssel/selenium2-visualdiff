package org.kreyssel.selenium.visualdiff.mojo.report;

import java.util.List;

import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.reporting.AbstractMavenReportRenderer;
import org.kreyssel.selenium.visualdiff.core.diff.VisualDiffMeta;
import org.kreyssel.selenium.visualdiff.core.diff.VisualDiffMetaGrouper;
import org.kreyssel.selenium.visualdiff.mojo.report.VisualDiffReportUtil.Depth;

import com.google.common.collect.ImmutableListMultimap;

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
		ImmutableListMultimap<String, VisualDiffMeta> perTest = VisualDiffMetaGrouper
				.byTestClass(diffs);
		VisualDiffReportUtil.renderTable(sink, Depth.CLASS, "Test Overview", perTest);

		for (String testClass : perTest.keySet()) {
			ImmutableListMultimap<String, VisualDiffMeta> perMethod = VisualDiffMetaGrouper
					.byTestMethod(perTest.get(testClass));
			VisualDiffReportUtil.renderTable(sink, Depth.METHOD, "Test " + testClass, perMethod);
		}
	}
}
