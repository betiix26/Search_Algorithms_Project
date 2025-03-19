package tree_entity;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;

import utils.MyLogger;

public class TreeGenerator {
	private static final Random RANDOM = new Random(); // Reusable Random instance

	// Private constructor to prevent instantiation
	private TreeGenerator() {
		throw new UnsupportedOperationException("Utility class");
	}

	public static void generateTree(int numberOfVertices, String fileName) {
		try (FileWriter writer = new FileWriter(fileName)) {
			writer.write(String.format("%d %d%n", numberOfVertices, numberOfVertices - 1));

			List<Integer> vertices = new ArrayList<>();
			for (int i = 0; i < numberOfVertices; i++) {
				vertices.add(i);
			}

			Collections.shuffle(vertices, RANDOM);

			for (int i = 1; i < numberOfVertices; i++) {
				int vertex1 = vertices.get(i);
				int vertex2 = vertices.get(RANDOM.nextInt(i));
				writer.write(String.format("%d %d%n", vertex1, vertex2));
			}
		} catch (IOException e) {
			MyLogger.log(Level.SEVERE, "An error occurred while writing to the file.");
			e.printStackTrace();
		}
	}
}
