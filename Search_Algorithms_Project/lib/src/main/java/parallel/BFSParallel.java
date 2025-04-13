package parallel;

import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;

import graph_entity.Graph;
import utils.MyLogger;
import utils.Node;
import utils.SearchMetrics;
import utils.SearchResult;

/**
 * A parallel implementation of Breadth-First Search (BFS) on a graph.
 * This class processes the graph in parallel using multiple threads.
 */
public class BFSParallel implements Callable<SearchResult> {
	
	// The graph to traverse
    private final Graph graph; 
    
    // The node where the traversal starts
    private final Node startNode; 
    
    // Queue to manage the nodes to be processed
    private final ConcurrentLinkedQueue<Node> queue = new ConcurrentLinkedQueue<>(); 
    
    // HashMap to track visited nodes
    private final ConcurrentHashMap<Node, Boolean> visited = new ConcurrentHashMap<>(); 

    private long computationTime = 0;
    private long communicationTime = 0;
    
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
    
    @Override
    public SearchResult call() {
        long startTime = System.nanoTime();
        long computationTime = 0;
        long communicationTime = 0;
        int nodesProcessed = 0;

        try {
            while (!queue.isEmpty()) {
                // Faza de calcul
                long computeStart = System.nanoTime();
                Node currentNode = queue.poll();
                if (currentNode == null) continue;
                
                nodesProcessed++;
                processNeighbors(currentNode);
                computationTime += System.nanoTime() - computeStart;
                
                // Faza de comunicare
                long commStart = System.nanoTime();
                // Sincronizare implicită
                communicationTime += System.nanoTime() - commStart;
            }
        } catch (Exception e) {
            MyLogger.log(Level.SEVERE, "BFS error: " + e.getMessage());
        }
        
        long totalTime = System.nanoTime() - startTime;
        SearchMetrics metrics = new SearchMetrics(totalTime, computationTime, communicationTime,
                               measureMemoryUsage(), "BFS", true, nodesProcessed);
        
        return new SearchResult(visited.keySet(), metrics);
    }
    
    private long measureMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();
        runtime.gc();
        return runtime.totalMemory() - runtime.freeMemory();
    }

}
