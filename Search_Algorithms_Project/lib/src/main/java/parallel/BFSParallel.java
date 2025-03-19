package parallel;

import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;

import graph_entity.Graph;
import utils.MyLogger;
import utils.Node;

public class BFSParallel implements Callable<Set<Node>> {
	private final Graph graph;
	private final Node startNode;
	private final ConcurrentLinkedQueue<Node> queue = new ConcurrentLinkedQueue<>();
	private final ConcurrentHashMap<Node, Boolean> visited = new ConcurrentHashMap<>();

	public BFSParallel(Graph graph, Node startNode) {
		this.graph = graph;
		this.startNode = startNode;
		this.queue.add(startNode);
		this.visited.put(startNode, true);
	}

	@Override
	public Set<Node> call() {
		try {
			while (!queue.isEmpty()) {
				Node currentNode = queue.poll();
				if (currentNode == null)
					continue;

				try {
					Set<Node> neighbors = graph.getNeighbors(currentNode);
					for (Node neighbor : neighbors) {
						if (visited.putIfAbsent(neighbor, true) == null) {
							queue.add(neighbor);
						}
					}
				} catch (Exception e) {
					MyLogger.log(Level.SEVERE,
							"Failed to process neighbors for node " + currentNode.getId() + ": " + e.getMessage());
				}
			}
		} catch (Exception e) {
			MyLogger.log(Level.SEVERE, "An error occurred during graphBFS: " + e.getMessage());
		}
		return visited.keySet();
	}
}