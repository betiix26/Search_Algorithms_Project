package graph_entity;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.logging.Level;

import utils.MyLogger;

public class GraphGenerator {

	private static final Random RANDOM = new Random(); // Reusable Random instance

	private GraphGenerator() {
		throw new UnsupportedOperationException("Utility class");
	}

	public static void generateGraph(int numberOfVertices, int numberOfEdges, String fileName) {
		if (numberOfEdges > (long) numberOfVertices * (numberOfVertices - 1) / 2) {
			MyLogger.log(Level.SEVERE, "The number of edges is too large for the given number of nodes.");
			return;
		}

		try (FileWriter writer = new FileWriter(fileName)) {
			writer.write(String.format("%d %d%n", numberOfVertices, numberOfEdges));

			for (int i = 0; i < numberOfEdges; i++) {
				int vertex1 = RANDOM.nextInt(numberOfVertices);
				int vertex2 = RANDOM.nextInt(numberOfVertices);
				while (vertex1 == vertex2) {
					vertex2 = RANDOM.nextInt(numberOfVertices);
				}

				writer.write(String.format("%d %d%n", vertex1, vertex2));
			}
		} catch (IOException e) {
			MyLogger.log(Level.SEVERE, "An error occurred while writing to the file.");
			e.printStackTrace();
		}
	}
}
