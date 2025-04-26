package tree_entity;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;

import utils.MyLogger;

/**
 * Utility class for generating random trees and saving them to a file.
 */
public class TreeGenerator {

    private static final Random RANDOM = new Random(); // Reusable Random instance

    // Private constructor to prevent instantiation
    private TreeGenerator() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Generates a random tree with the specified number of vertices and saves it to a file.
     * The tree is created by shuffling nodes and randomly connecting them to form a valid structure.
     *
     * @param numberOfVertices The number of nodes in the tree.
     * @param fileName         The output file where the tree structure will be saved.
     */
    public static void generateConnectedTree(int numberOfVertices, String fileName) throws IOException {
        try (FileWriter writer = new FileWriter(fileName)) {
            // Create a list of nodes
            List<Integer> vertices = new ArrayList<>();
            for (int i = 0; i < numberOfVertices; i++) {
                vertices.add(i);
            }

            // Shuffle the nodes randomly
            Collections.shuffle(vertices, RANDOM);

            // We'll build the tree structure in memory first
            Map<Integer, List<Integer>> treeStructure = new HashMap<>();
            for (int i = 0; i < numberOfVertices; i++) {
                treeStructure.put(vertices.get(i), new ArrayList<>());
            }

            // Connect each node (except root) to a random parent
            for (int i = 1; i < numberOfVertices; i++) {
                int child = vertices.get(i);
                int parent = vertices.get(RANDOM.nextInt(i)); // Parent is from already connected nodes
                treeStructure.get(parent).add(child);
            }

            // Write to file in the expected format (each line: node child1 child2 ...)
            for (Map.Entry<Integer, List<Integer>> entry : treeStructure.entrySet()) {
                writer.write(entry.getKey().toString());
                for (Integer child : entry.getValue()) {
                    writer.write(" " + child);
                }
                writer.write("\n");
            }
        }
    }
    
    public static void generateTree(int numberOfVertices, String fileName) {
        try (FileWriter writer = new FileWriter(fileName)) {
            // A tree with N nodes always has N-1 edges
            writer.write(String.format("%d %d%n", numberOfVertices, numberOfVertices - 1));

            // Create a list of nodes
            List<Integer> vertices = new ArrayList<>();
            for (int i = 0; i < numberOfVertices; i++) {
                vertices.add(i);
            }

            // Shuffle the nodes randomly
            Collections.shuffle(vertices, RANDOM);

            // Connect each node to a random previous node, ensuring a valid tree structure
            for (int i = 1; i < numberOfVertices; i++) {
                int vertex1 = vertices.get(i);
                int vertex2 = vertices.get(RANDOM.nextInt(i)); // Connect to a node that was added earlier
                writer.write(String.format("%d %d%n", vertex1, vertex2));
            }
        } catch (IOException e) {
            MyLogger.log(Level.SEVERE, "An error occurred while writing to the file.");
            e.printStackTrace();
        }
    }
}
