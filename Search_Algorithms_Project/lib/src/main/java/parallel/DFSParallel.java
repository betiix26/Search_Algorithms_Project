package parallel;

import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.logging.Level;

import graph_entity.Graph;
import utils.MyLogger;
import utils.Node;

public class DFSParallel implements Callable<Set<Node>> {
	private final Graph graph;
	private final Node startNode;
	private final ConcurrentLinkedDeque<Node> stack = new ConcurrentLinkedDeque<>();
	private final ConcurrentHashMap<Node, Boolean> visited = new ConcurrentHashMap<>();

	public DFSParallel(Graph graph, Node startNode) {
		this.graph = graph;
		this.startNode = startNode;
	}

	@Override
	public Set<Node> call() {
		try {
			stack.push(startNode);
			visited.put(startNode, true);

			while (!stack.isEmpty()) {
				Node currentNode = stack.pop();
				graph.getNeighbors(currentNode).forEach(neighbor -> {
					if (visited.putIfAbsent(neighbor, true) == null) {
						stack.push(neighbor);
					}
				});
			}
		} catch (Exception e) {
			MyLogger.log(Level.SEVERE, "An error occurred during graphDFS: " + e.getMessage());
		}
		return visited.keySet();
	}
}
