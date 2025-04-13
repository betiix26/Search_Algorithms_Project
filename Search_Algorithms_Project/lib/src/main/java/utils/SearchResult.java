package utils;

import java.util.Set;

public class SearchResult {
	private final Set<Node> visitedNodes;
	private final SearchMetrics metrics;

	public SearchResult(Set<Node> visitedNodes, SearchMetrics metrics) {
		this.visitedNodes = visitedNodes;
		this.metrics = metrics;
	}

	// Getters
	public Set<Node> getVisitedNodes() {
		return visitedNodes;
	}

	public SearchMetrics getMetrics() {
		return metrics;
	}
}