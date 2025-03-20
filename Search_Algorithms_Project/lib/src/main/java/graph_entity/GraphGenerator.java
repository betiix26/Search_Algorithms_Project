package graph_entity;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.logging.Level;

import utils.MyLogger;

/**
 * A utility class to generate a random graph and save it to a file.
 */
public class GraphGenerator {

    // Reusable Random instance for generating random numbers
    private static final Random RANDOM = new Random();

    // Private constructor to prevent instantiation of the utility class
    private GraphGenerator() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Generates a random graph and writes it to a file.
     * 
     * @param numberOfVertices The number of vertices (nodes) in the graph.
     * @param numberOfEdges    The number of edges to create between vertices.
     * @param fileName         The name of the file to save the graph.
     */
    public static void generateGraph(int numberOfVertices, int numberOfEdges, String fileName) {
    	
        // Check if the number of edges is valid for the given number of vertices
        if (numberOfEdges > (long) numberOfVertices * (numberOfVertices - 1) / 2) {
            MyLogger.log(Level.SEVERE, "The number of edges is too large for the given number of nodes.");
            return;
        }

        try (FileWriter writer = new FileWriter(fileName)) {
        	
            // Write the graph metadata: number of vertices and edges
            writer.write(String.format("%d %d%n", numberOfVertices, numberOfEdges));

            // Generate and write random edges to the file
            for (int i = 0; i < numberOfEdges; i++) {
                int vertex1 = RANDOM.nextInt(numberOfVertices); // Random vertex 1
                int vertex2 = RANDOM.nextInt(numberOfVertices); // Random vertex 2
                
                // Ensure vertex1 and vertex2 are not the same (no self-loops)
                while (vertex1 == vertex2) {
                    vertex2 = RANDOM.nextInt(numberOfVertices);
                }

                // Write the edge to the file
                writer.write(String.format("%d %d%n", vertex1, vertex2));
            }
        } catch (IOException e) {
        	
            // Log any IO exceptions that occur during file writing
            MyLogger.log(Level.SEVERE, "An error occurred while writing to the file.");
            e.printStackTrace();
        }
    }
}
