package parallel;

import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.logging.Level;

import graph_entity.Graph;
import utils.MyLogger;
import utils.Node;
import utils.SearchMetrics;
import utils.SearchResult;

/**
 * A parallel implementation of Depth-First Search (DFS) on a graph. This class
 * processes the graph in parallel using multiple threads.
 */
public class DFSParallel implements Callable<SearchResult> {

	// The graph to traverse
	private final Graph graph;

	// The starting node for DFS
	private final Node startNode;

	// Stack for DFS traversal
	private final ConcurrentLinkedDeque<Node> stack = new ConcurrentLinkedDeque<>();

	// Tracks visited nodes
	private final ConcurrentHashMap<Node, Boolean> visited = new ConcurrentHashMap<>();

	private long computationTime = 0;
	private long communicationTime = 0;

	/**
	 * Constructor to initialize DFS with the graph and the start node.
	 *
	 * @param graph     The graph on which DFS is performed.
	 * @param startNode The node from which DFS starts.
	 */
	public DFSParallel(Graph graph, Node startNode) {
		this.graph = graph;
		this.startNode = startNode;
	}

	/**
	 * The call method is executed by the thread and performs the DFS traversal.
	 * 
	 * @return A set of visited nodes.
	 */

	@Override
    public SearchResult call() {
        long startTime = System.nanoTime();
        long computationTime = 0;
        long communicationTime = 0;
        int nodesProcessed = 0;

        try {
            stack.push(startNode);
            visited.put(startNode, true);

            while (!stack.isEmpty()) {
                // Computation phase
                long computeStart = System.nanoTime();
                Node currentNode = stack.pop();
                nodesProcessed++;
                
                for (Node neighbor : graph.getNeighbors(currentNode)) {
                    if (visited.putIfAbsent(neighbor, true) == null) {
                        stack.push(neighbor);
                    }
                }
                computationTime += System.nanoTime() - computeStart;
                
                // Communication phase
                long commStart = System.nanoTime();
                // Implicit synchronization measured here
                communicationTime += System.nanoTime() - commStart;
            }
        } catch (Exception e) {
            MyLogger.log(Level.SEVERE, "DFS error: " + e.getMessage());
        }
        
        long totalTime = System.nanoTime() - startTime;
        SearchMetrics metrics = new SearchMetrics(totalTime, computationTime, communicationTime,
                               measureMemoryUsage(), "DFS", true, nodesProcessed);
        
        return new SearchResult(visited.keySet(), metrics);  // Return SearchResult instead of SearchMetrics
    }
    
    private long measureMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();
        runtime.gc();
        return runtime.totalMemory() - runtime.freeMemory();
    }
}
