package org.kreyssel.selenium.visualdiff.core.diff;

import java.util.List;

import org.kreyssel.selenium.visualdiff.core.diff.VisualDiffMeta.Type;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Multimaps;

/**
 * Grouped by test-class, test-method, screenshot-id
 * 
 * @author conny
 * 
 */
public final class VisualDiffMetaGrouper {

	private VisualDiffMetaGrouper() {
	}

	public static ImmutableListMultimap<String, VisualDiffMeta> byTestClass(
			final List<VisualDiffMeta> diffs) {

		ImmutableListMultimap<String, VisualDiffMeta> byTest = Multimaps.index(diffs,
				new Function<VisualDiffMeta, String>() {
					public String apply(final VisualDiffMeta input) {
						return input.testClass;
					}
				});

		return byTest;
	}

	public static ImmutableListMultimap<String, VisualDiffMeta> byTestMethod(
			final List<VisualDiffMeta> diffsPerTest) {

		ImmutableListMultimap<String, VisualDiffMeta> byMethod = Multimaps.index(diffsPerTest,
				new Function<VisualDiffMeta, String>() {
					public String apply(final VisualDiffMeta input) {
						return input.testMethod;
					}
				});

		return byMethod;
	}

	public static int countByDiffType(final List<VisualDiffMeta> diffs, final Type diffType) {
		int count = 0;
		for (VisualDiffMeta diff : diffs) {
			if (diff.diffType == diffType) {
				count++;
			}
		}
		return count;
	}
}