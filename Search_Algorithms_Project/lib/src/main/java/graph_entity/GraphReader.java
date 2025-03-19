package graph_entity;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class GraphReader {

	private GraphReader() {
		throw new UnsupportedOperationException("This is a utility class and cannot be instantiated.");
	}

	public static List<List<Integer>> readGraph(String fileName) throws IOException {
		File file = new File(fileName);

		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line = br.readLine();
			String[] parts = line.split(" ");

			int numberOfVertices = Integer.parseInt(parts[0]);
			List<List<Integer>> adjacencyList = new ArrayList<>(numberOfVertices);
			for (int i = 0; i < numberOfVertices; i++) {
				adjacencyList.add(new ArrayList<>());
			}

			while ((line = br.readLine()) != null) {
				parts = line.split(" ");
				int u = Integer.parseInt(parts[0]);
				int v = Integer.parseInt(parts[1]);
				adjacencyList.get(u).add(v);
				adjacencyList.get(v).add(u);
			}

			return adjacencyList;
		}
	}
}
