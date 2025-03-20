package parallel;

import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;

import graph_entity.Graph;
import utils.MyLogger;
import utils.Node;

/**
 * A parallel implementation of Breadth-First Search (BFS) on a graph.
 * This class processes the graph in parallel using multiple threads.
 */
public class BFSParallel implements Callable<Set<Node>> {
	
	// The graph to traverse
    private final Graph graph; 
    
    // The node where the traversal starts
    private final Node startNode; 
    
    // Queue to manage the nodes to be processed
    private final ConcurrentLinkedQueue<Node> queue = new ConcurrentLinkedQueue<>(); 
    
    // HashMap to track visited nodes
    private final ConcurrentHashMap<Node, Boolean> visited = new ConcurrentHashMap<>(); 

    /**
     * Constructor to initialize BFS with the graph and the start node.
     *
     * @param graph The graph on which BFS is performed.
     * @param startNode The node from which BFS starts.
     */
    public BFSParallel(Graph graph, Node startNode) {
        this.graph = graph;
        this.startNode = startNode;
        this.queue.add(startNode); // Add the start node to the queue
        this.visited.put(startNode, true); // Mark the start node as visited
    }

    /**
     * The call method is executed by the thread and performs the BFS traversal.
     * 
     * @return A set of visited nodes.
     */
    @Override
    public Set<Node> call() {
        try {
            // Continue processing as long as there are nodes in the queue
            while (!queue.isEmpty()) {
                Node currentNode = queue.poll();
                if (currentNode == null) continue; // Skip if no node is available

                // Process the neighbors of the current node
                processNeighbors(currentNode);
            }
        } catch (Exception e) {
            // Log any errors that occur during the BFS process
            MyLogger.log(Level.SEVERE, "An error occurred during Graph BFS: " + e.getMessage());
        }
        // Return the set of visited nodes
        return visited.keySet();
    }

    /**
     * Processes the neighbors of a node and adds unvisited neighbors to the queue.
     * 
     * @param currentNode The node whose neighbors are being processed.
     */
    private void processNeighbors(Node currentNode) {
        try {
            // Retrieve the neighbors of the current node
            Set<Node> neighbors = graph.getNeighbors(currentNode);
            for (Node neighbor : neighbors) {
                // If the neighbor has not been visited, mark it as visited and add it to the queue
                if (visited.putIfAbsent(neighbor, true) == null) {
                    queue.add(neighbor);
                }
            }
        } catch (Exception e) {
            // Log any errors that occur while processing neighbors
            MyLogger.log(Level.SEVERE, "Failed to process neighbors for node " + currentNode.getId() + ": " + e.getMessage());
        }
    }
}
