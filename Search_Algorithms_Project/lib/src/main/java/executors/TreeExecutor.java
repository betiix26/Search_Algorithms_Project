package executors;

import sequential.BFSSequential;
import sequential.DFSSequential;
import tree_entity.TreeNode;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.*;
import java.util.logging.Logger;

import excel.ExcelDataRecorder;

public class TreeExecutor {

	private TreeExecutor() {
		throw new UnsupportedOperationException("This is a utility class and cannot be instantiated.");
	}

	private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	/**
     * Executes the BFS algorithm on a tree read from a file, measuring execution time and memory usage.
     *
     * @param fileName     The name of the file containing the tree representation.
     * @param bfsTimes     A list to store BFS execution times.
     * @param memoryUsage  A list to store memory usage for BFS.
     * @throws IOException If an error occurs while reading the file.
     */
	public static void runTreeBFS(String fileName, List<Double> bfsTimes, List<Long> memoryUsage) throws IOException {
		
		// Measure memory usage before execution
		MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
		MemoryUsage beforeMem = memoryBean.getHeapMemoryUsage();
		long beforeUsedMem = beforeMem.getUsed();

		// Read the tree from file and execute BFS
		TreeNode root = readTreeFromFile(fileName);
		long startTime = System.nanoTime();
		BFSSequential.treeBFS(root);
		long endTime = System.nanoTime();

		// Measure memory usage after execution
		MemoryUsage afterMem = memoryBean.getHeapMemoryUsage();
		long afterUsedMem = afterMem.getUsed();
		memoryUsage.add(afterUsedMem - beforeUsedMem);
		System.out.printf("%nSequential Tree BFS memory usage (bytes): %d%n", afterUsedMem - beforeUsedMem);

		// Calculate and store execution time
		double durationInSeconds = (endTime - startTime) / 1_000_000_000.0;
		System.out.printf("%nTree BFS execution time (iterative): %.9f seconds.%n", durationInSeconds);
		bfsTimes.add(durationInSeconds);
	}

	/**
     * Executes the DFS algorithm on a tree read from a file, measuring execution time and memory usage.
     *
     * @param fileName     The name of the file containing the tree representation.
     * @param dfsTimes     A list to store DFS execution times.
     * @param memoryUsage  A list to store memory usage for DFS.
     * @throws IOException If an error occurs while reading the file.
     */
	public static void runTreeDFS(String fileName, List<Double> dfsTimes, List<Long> memoryUsage) throws IOException {
		
		// Measure memory usage before execution
		MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
		MemoryUsage beforeMem = memoryBean.getHeapMemoryUsage();
		long beforeUsedMem = beforeMem.getUsed();

		// Read the tree from file and execute DFS
		TreeNode root = readTreeFromFile(fileName);
		long startTime = System.nanoTime();
		DFSSequential.treeDFS(root);
		long endTime = System.nanoTime();

		// Measure memory usage after execution
		MemoryUsage afterMem = memoryBean.getHeapMemoryUsage();
		long afterUsedMem = afterMem.getUsed();
		memoryUsage.add(afterUsedMem - beforeUsedMem);
		System.out.printf("%nSequential Tree DFS memory usage (bytes): %d%n", afterUsedMem - beforeUsedMem);

		// Calculate and store execution time
		double durationInSeconds = (endTime - startTime) / 1_000_000_000.0;
		System.out.printf("%nTree DFS execution time (iterative): %.9f seconds.%n", durationInSeconds);
		dfsTimes.add(durationInSeconds);
	}

	/**
     * Reads a tree structure from a file and reconstructs it as a tree of {@code TreeNode} objects.
     *
     * @param  fileName The name of the file containing the tree representation.
     * @return The root node of the reconstructed tree.
     * @throws IOException If an error occurs while reading the file.
     */
	public static TreeNode readTreeFromFile(String fileName) throws IOException {
		Map<Integer, TreeNode> nodes = new HashMap<>();

		try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
			String line;
			while ((line = reader.readLine()) != null) {
				String[] parts = line.split(" ");
				int nodeId = Integer.parseInt(parts[0]);
				TreeNode node = nodes.computeIfAbsent(nodeId, TreeNode::new);

				// Add child nodes
				for (int i = 1; i < parts.length; i++) {
					int childId = Integer.parseInt(parts[i]);
					TreeNode child = nodes.computeIfAbsent(childId, TreeNode::new);
					node.addChild(child);
				}
			}
		}

		// Assuming node with ID 0 is the root
		return nodes.get(0);
	}

	/**
     * Runs both BFS and DFS on the tree and stores execution time and memory usage results in an Excel file.
     *
     * @param fileName  The file containing the tree data.
     * @param scanner   A {@code Scanner} instance for user input (not used in this method).
     * @param nodeCount The number of nodes in the tree.
     */
	public static void runTreeMethods(String fileName, Scanner scanner, int nodeCount) {
		List<Double> bfsTreeTimes = new ArrayList<>();
		List<Double> dfsTreeTimes = new ArrayList<>();
		List<Long> bfsTreeMemoryUsage = new ArrayList<>();
		List<Long> dfsTreeMemoryUsage = new ArrayList<>();

		try {
			runTreeBFS(fileName, bfsTreeTimes, bfsTreeMemoryUsage);
			runTreeDFS(fileName, dfsTreeTimes, dfsTreeMemoryUsage);
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			// Save execution times to Excel
			ExcelDataRecorder.writeData("SequentialExecutionTimes.xlsx", bfsTreeTimes, dfsTreeTimes, nodeCount, true, true);
			
			// Save memory usage data to Excel (converted to Double)
			ExcelDataRecorder.writeData("SequentialMemoryUsage.xlsx", convertToDoubleList(bfsTreeMemoryUsage),
					convertToDoubleList(dfsTreeMemoryUsage), nodeCount, false, true);
		} catch (IOException e) {
			LOGGER.severe("Failed to write tree execution times to Excel for " + nodeCount + " nodes.");
		}
	}

	/**
     * Converts a list of {@code Long} values to a list of {@code Double} values.
     *
     * @param  longList The list of {@code Long} values to be converted.
     * @return A new list containing the converted {@code Double} values.
     */
	public static List<Double> convertToDoubleList(List<Long> longList) {
		List<Double> doubleList = new ArrayList<>();
		for (Long value : longList) {
			doubleList.add(value.doubleValue());
		}
		return doubleList;
	}
}
