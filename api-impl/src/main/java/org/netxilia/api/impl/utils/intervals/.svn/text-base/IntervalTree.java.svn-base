package org.netxilia.api.impl.utils.intervals;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class IntervalTree<V> extends TreeMap<Interval, V> {

	private static final long serialVersionUID = 1L;

	public void recalculate() {
		if (getRoot() != null) {
			recalculate(getRoot());
		}
	}

	private void recalculate(Entry<Interval, V> entry) {
		int min = entry.getKey().getStart();
		int max = entry.getKey().getEnd();
		if (entry.left != null) {
			recalculate(entry.left);
			max = Math.max(max, entry.left.key.getMax());
			min = Math.min(min, entry.left.key.getMin());
		}
		if (entry.right != null) {
			recalculate(entry.right);
			max = Math.max(max, entry.right.key.getMax());
			min = Math.min(min, entry.right.key.getMin());
		}
		entry.getKey().setMin(min);
		entry.getKey().setMax(max);
	}

	public List<Interval> searchIntervals(Interval search) {
		List<Map.Entry<Interval, V>> results = new ArrayList<Map.Entry<Interval, V>>();
		search(getRoot(), search, results);
		List<Interval> intervals = new ArrayList<Interval>(results.size());
		for (Map.Entry<Interval, V> entry : results) {
			intervals.add(entry.getKey());
		}
		return intervals;
	}

	public List<Map.Entry<Interval, V>> searchEntries(Interval search) {
		List<Map.Entry<Interval, V>> results = new ArrayList<Map.Entry<Interval, V>>();
		search(getRoot(), search, results);
		return results;
	}

	private void search(Entry<Interval, V> entry, Interval search, List<Map.Entry<Interval, V>> result) {
		// Don't search nodes that don't exist
		if (entry == null) {
			return;
		}

		// Search left children
		if (entry.left != null && entry.left.key.getMax() >= search.getStart()) {
			search(entry.left, search, result);
		}

		// Check this node
		if (entry.key.intersects(search)) {
			result.add(entry);
		}

		// Otherwise, search right children
		if (entry.right != null && entry.right.key.getMin() <= search.getEnd()) {
			search(entry.right, search, result);
		}
	}

}
