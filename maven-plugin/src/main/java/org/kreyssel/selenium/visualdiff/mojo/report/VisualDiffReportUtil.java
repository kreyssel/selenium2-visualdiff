package org.kreyssel.selenium.visualdiff.mojo.report;

import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.util.DoxiaUtils;
import org.kreyssel.selenium.visualdiff.core.diff.VisualDiffMeta;
import org.kreyssel.selenium.visualdiff.core.diff.VisualDiffMeta.Type;
import org.kreyssel.selenium.visualdiff.core.diff.VisualDiffMetaGrouper;

import com.google.common.collect.ImmutableListMultimap;

public final class VisualDiffReportUtil {

	public enum Depth {
		CLASS, METHOD, SCRENSHOT;
	}

	static void renderTable(final Sink sink, final Depth depth, final String title,
			final ImmutableListMultimap<String, VisualDiffMeta> grouping) {

		sink.section2();
		sink.sectionTitle2();
		sink.text(title);
		sink.sectionTitle2_();

		sink.table();

		renderTableHeaders(sink);

		for (String testElement : grouping.keySet()) {
			VisualDiffMeta first = grouping.get(testElement).get(0);

			sink.tableRow();

			sink.tableCell();

			String anchor = depth == Depth.CLASS ? "" : "Test Method " + testElement;
			sink.link(VisualDiffReportUtil.asFilename(first.testClass, ".html") + "#"
					+ DoxiaUtils.encodeId(anchor, true));
			sink.text(testElement);
			sink.link_();

			sink.tableCell_();

			sink.tableCell();
			sink.text(Integer.toString(grouping.get(testElement).size()));
			sink.tableCell_();

			sink.tableCell();
			sink.text(Integer.toString(VisualDiffMetaGrouper.countByDiffType(
					grouping.get(testElement), Type.EQUAL)));
			sink.tableCell_();

			sink.tableCell();
			sink.text(Integer.toString(VisualDiffMetaGrouper.countByDiffType(
					grouping.get(testElement), Type.DIFFERENT)));
			sink.tableCell_();

			sink.tableCell();
			sink.text(Integer.toString(VisualDiffMetaGrouper.countByDiffType(
					grouping.get(testElement), Type.ADDED)));
			sink.tableCell_();

			sink.tableCell();
			sink.text(Integer.toString(VisualDiffMetaGrouper.countByDiffType(
					grouping.get(testElement), Type.REMOVED)));
			sink.tableCell_();

			sink.tableRow_();
		}

		renderTableHeaders(sink);

		sink.table_();

		if (depth == Depth.CLASS)
			sink.section1_();
		else
			sink.section2_();
	}

	private static void renderTableHeaders(final Sink sink) {
		sink.tableRow();

		sink.tableHeaderCell();
		sink.text("Test Class");
		sink.tableHeaderCell_();

		sink.tableHeaderCell();
		sink.text("Total Screenshots");
		sink.tableHeaderCell_();

		sink.tableHeaderCell();
		sink.text("Equal");
		sink.tableHeaderCell_();

		sink.tableHeaderCell();
		sink.text("Different");
		sink.tableHeaderCell_();

		sink.tableHeaderCell();
		sink.text("Added");
		sink.tableHeaderCell_();

		sink.tableHeaderCell();
		sink.text("Removed");
		sink.tableHeaderCell_();

		sink.tableRow_();
	}

	public static String asFilename(final String testClass, final String suffix) {
		return "visualdiff_" + testClass.replace('.', '_') + suffix;
	}
}
