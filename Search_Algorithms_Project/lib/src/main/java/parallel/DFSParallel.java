package parallel;

import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.logging.Level;

import graph_entity.Graph;
import utils.MyLogger;
import utils.Node;

/**
 * A parallel implementation of Depth-First Search (DFS) on a graph.
 * This class processes the graph in parallel using multiple threads.
 */
public class DFSParallel implements Callable<Set<Node>> {
	
	// The graph to traverse
    private final Graph graph;
    
    // The starting node for DFS
    private final Node startNode; 
    
    // Stack for DFS traversal
    private final ConcurrentLinkedDeque<Node> stack = new ConcurrentLinkedDeque<>(); 
    
    // Tracks visited nodes
    private final ConcurrentHashMap<Node, Boolean> visited = new ConcurrentHashMap<>(); 

    /**
     * Constructor to initialize DFS with the graph and the start node.
     *
     * @param graph The graph on which DFS is performed.
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
    public Set<Node> call() {
        try {
            // Push the start node onto the stack and mark it as visited
            stack.push(startNode);
            visited.put(startNode, true);

            // Continue traversal while there are nodes in the stack
            while (!stack.isEmpty()) {
                Node currentNode = stack.pop(); // Remove the top node from the stack

                // Iterate over all neighbors of the current node
                graph.getNeighbors(currentNode).forEach(neighbor -> {
                    // If the neighbor hasn't been visited, mark it and push it onto the stack
                    if (visited.putIfAbsent(neighbor, true) == null) {
                        stack.push(neighbor);
                    }
                });
            }
        } catch (Exception e) {
            // Log any errors encountered during DFS execution
            MyLogger.log(Level.SEVERE, "An error occurred during Graph DFS: " + e.getMessage());
        }
        // Return the set of visited nodes
        return visited.keySet();
    }
}
