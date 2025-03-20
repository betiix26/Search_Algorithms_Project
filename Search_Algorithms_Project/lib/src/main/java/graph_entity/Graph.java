package graph_entity;

import java.util.*;
import java.util.logging.Level;

import utils.MyLogger;
import utils.Node;

/**
 * Represents a graph using an adjacency list.
 */
public class Graph {
    // Adjacency list representation of the graph
    private Map<Node, Set<Node>> adjacencyList = new HashMap<>();

    /**
     * Adds an edge between two nodes in the graph.
     *
     * @param source      The source node.
     * @param destination The destination node.
     */
    public void addEdge(Node source, Node destination) {
        try {
            // Ensure both nodes exist in the adjacency list
            this.adjacencyList.putIfAbsent(source, new HashSet<>());
            this.adjacencyList.putIfAbsent(destination, new HashSet<>());

            // Add directed edge from source to destination
            this.adjacencyList.get(source).add(destination);

            // Uncomment the following line to make the graph undirected (bidirectional edges)
            this.adjacencyList.get(destination).add(source);
        } catch (Exception e) {
            MyLogger.log(Level.SEVERE, "Failed to add edge from " + source + " to " + destination + ": " + e.getMessage());
        }
    }

    /**
     * Retrieves the neighbors (adjacent nodes) of a given node.
     *
     * @param  node The node whose neighbors are to be retrieved.
     * @return A set of adjacent nodes, or an empty set if the node has no neighbors.
     */
    public Set<Node> getNeighbors(Node node) {
        try {
            Set<Node> neighbors = this.adjacencyList.get(node);
            return (neighbors != null) ? new HashSet<>(neighbors) : Collections.emptySet();
        } catch (Exception e) {
            MyLogger.log(Level.SEVERE, "Failed to get neighbors for " + node + ": " + e.getMessage());
            return Collections.emptySet();
        }
    }

    /**
     * Prints the graph in an adjacency list format.
     */
    public void printGraph() {
        for (Node node : adjacencyList.keySet()) {
            System.out.print("Node " + node.getId() + " connects to: ");
            for (Node connectedNode : getNeighbors(node)) {
                System.out.print(connectedNode.getId() + " ");
            }
            System.out.println();
        }
    }
}
