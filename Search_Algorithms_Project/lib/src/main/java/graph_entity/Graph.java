package graph_entity;

import java.util.*;
import java.util.logging.Level;

import utils.MyLogger;
import utils.Node;

public class Graph {
    private Map<Node, Set<Node>> adjacencyList = new HashMap<>();

    public void addEdge(Node source, Node destination) {
        try {
            this.adjacencyList.putIfAbsent(source, new HashSet<>());
            this.adjacencyList.putIfAbsent(destination, new HashSet<>());
            this.adjacencyList.get(source).add(destination);
            // Uncomment the following line for undirected graphs
			// this.adjacencyList.get(destination).add(source);
        } catch (Exception e) {
            MyLogger.log(Level.SEVERE, "Failed to add edge from " + source + " to " + destination + ": " + e.getMessage());
        }
    }

    // Returns the neighbors of a node
    public Set<Node> getNeighbors(Node node) {
        try {
            Set<Node> neighbors = this.adjacencyList.get(node);
            if (neighbors == null) {
                return Collections.emptySet();
            }
            return new HashSet<>(neighbors); 
        } catch (Exception e) {
            MyLogger.log(Level.SEVERE, "Failed to get neighbors for " + node + ": " + e.getMessage());
            return Collections.emptySet();
        }
    }

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
