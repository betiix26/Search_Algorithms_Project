package graph_entity;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class to read a graph from a file and return it as an adjacency list.
 */
public class GraphReader {

    // Private constructor to prevent instantiation of the utility class
    private GraphReader() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated.");
    }

    /**
     * Reads a graph from a file and returns it as an adjacency list.
     *
     * @param  fileName The name of the file containing the graph data.
     * @return A list of lists representing the adjacency list of the graph.
     * @throws IOException If an I/O error occurs while reading the file.
     */
    public static List<List<Integer>> readGraph(String fileName) throws IOException {
        // Create a File object from the file name
        File file = new File(fileName);

        // Use try-with-resources to automatically close the BufferedReader
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            // Read the first line containing the number of vertices and edges
            String line = br.readLine();
            String[] parts = line.split(" ");

            // Parse the number of vertices from the first part of the line
            int numberOfVertices = Integer.parseInt(parts[0]);

            // Initialize the adjacency list with an empty list for each vertex
            List<List<Integer>> adjacencyList = new ArrayList<>(numberOfVertices);
            for (int i = 0; i < numberOfVertices; i++) {
                adjacencyList.add(new ArrayList<>());
            }

            // Read the remaining lines that describe the edges of the graph
            while ((line = br.readLine()) != null) {
                parts = line.split(" ");
                int u = Integer.parseInt(parts[0]); // Start vertex of the edge
                int v = Integer.parseInt(parts[1]); // End vertex of the edge

                // Add the edge in both directions (for an undirected graph)
                adjacencyList.get(u).add(v);
                adjacencyList.get(v).add(u);
            }

            // Return the constructed adjacency list
            return adjacencyList;
        }
    }
}
