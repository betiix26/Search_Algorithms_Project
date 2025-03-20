package executors;

import parallel.BFSParallel;
import parallel.DFSParallel;
import sequential.BFSSequential;
import sequential.DFSSequential;
import utils.Node;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import excel.ExcelDataRecorder;
import graph_entity.Graph;
import graph_entity.GraphReader;

public class GraphExecutor {

	private GraphExecutor() {
		throw new UnsupportedOperationException("This is a utility class and cannot be instantiated.");
	}

	/**
	 * Executes BFS sequentially on a graph and records execution time and memory
	 * usage.
	 *
	 * @param fileName     Path to the graph file.
	 * @param startNodeID  The starting node ID for BFS traversal.
	 * @param bfsTimes     List to store BFS execution times.
	 * @param memoryUsage  List to store memory usage.
	 * @throws IOException If an error occurs during file reading.
	 */
	public static void runSequentialBFS(String fileName, int startNodeID, List<Double> bfsTimes,
			List<Double> memoryUsage) throws IOException {
		MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
		MemoryUsage beforeMem = memoryBean.getHeapMemoryUsage();
		long beforeUsedMem = beforeMem.getUsed();

		List<List<Integer>> adjacencyList = GraphReader.readGraph(fileName);
		long startTime = System.nanoTime();
		BFSSequential.graphBFS(adjacencyList, startNodeID);
		long endTime = System.nanoTime();

		MemoryUsage afterMem = memoryBean.getHeapMemoryUsage();
		long afterUsedMem = afterMem.getUsed();
		memoryUsage.add((double) (afterUsedMem - beforeUsedMem));
		System.out.printf("%nSequential Graph BFS memory usage (bytes): %d%n", afterUsedMem - beforeUsedMem);

		double durationInSeconds = (endTime - startTime) / 1_000_000_000.0;
		System.out.printf("%nGraph BFS execution time (iterative): %.9f seconds.%n", durationInSeconds);
		bfsTimes.add(durationInSeconds);
	}

	/**
	 * Executes DFS sequentially on a graph and records execution time and memory
	 * usage.
	 *
	 * @param fileName     Path to the graph file.
	 * @param startNodeID  The starting node ID for DFS traversal.
	 * @param dfsTimes     List to store DFS execution times.
	 * @param memoryUsage  List to store memory usage.
	 * @throws IOException If an error occurs during file reading.
	 */
	public static void runSequentialDFS(String fileName, int startNodeID, List<Double> dfsTimes,
			List<Double> memoryUsage) throws IOException {
		MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
		MemoryUsage beforeMem = memoryBean.getHeapMemoryUsage();
		long beforeUsedMem = beforeMem.getUsed();

		List<List<Integer>> adjacencyList = GraphReader.readGraph(fileName);
		long startTime = System.nanoTime();
		DFSSequential.graphDFS(adjacencyList, startNodeID);
		long endTime = System.nanoTime();

		MemoryUsage afterMem = memoryBean.getHeapMemoryUsage();
		long afterUsedMem = afterMem.getUsed();
		memoryUsage.add((double) (afterUsedMem - beforeUsedMem));
		System.out.printf("%nSequential Graph DFS memory usage (bytes): %d%n", afterUsedMem - beforeUsedMem);

		// Time calculation
		double durationInSeconds = (endTime - startTime) / 1_000_000_000.0;
		System.out.printf("%nGraph DFS execution time (iterative): %.9f seconds.%n", durationInSeconds);
		dfsTimes.add(durationInSeconds);
	}

	/**
	 * Runs BFS and DFS sequentially and saves the results (execution times and
	 * memory usage) to an Excel file.
	 *
	 * @param fileName    Path to the graph file.
	 * @param startNodeID The starting node ID.
	 * @param nodeCount   Number of nodes in the graph.
	 */
	public static void runSequentialMethods(String fileName, int startNodeID, int nodeCount) {
		List<Double> bfsSequentialTimes = new ArrayList<>();
		List<Double> dfsSequentialTimes = new ArrayList<>();
		List<Double> bfsMemoryUsage = new ArrayList<>();
		List<Double> dfsMemoryUsage = new ArrayList<>();
		try {
			runSequentialBFS(fileName, startNodeID, bfsSequentialTimes, bfsMemoryUsage);
			runSequentialDFS(fileName, startNodeID, dfsSequentialTimes, dfsMemoryUsage);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			ExcelDataRecorder.writeData("SequentialExecutionTimes.xlsx", bfsSequentialTimes, dfsSequentialTimes,
					nodeCount, true, false);
			System.out.println(
					"Sequential execution times for " + nodeCount + " nodes saved to SequentialExecutionTimes.xlsx");

			ExcelDataRecorder.writeData("SequentialMemoryUsage.xlsx", bfsMemoryUsage, dfsMemoryUsage, nodeCount, false,
					false);
			System.out
					.println("Sequential memory usage for " + nodeCount + " nodes saved to SequentialMemoryUsage.xlsx");
		} catch (IOException e) {
			System.out.println("Failed to write sequential execution times to Excel for " + nodeCount + " nodes.");
			e.printStackTrace();
		}
	}

	/**
	 * Runs BFS and DFS in parallel using an ExecutorService.
	 *
	 * @param fileName    Path to the graph file.
	 * @param startNodeID The starting node ID.
	 */
	public static void runParallelMethods(String fileName, int startNodeID) throws Exception {
		List<List<Integer>> adj = GraphReader.readGraph(fileName);
		Graph graph = convertListToGraph(adj);
		Node startNode = new Node(startNodeID);

		ExecutorService executor = Executors.newFixedThreadPool(2);

		// For memory measurement
		MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();

		// Start memory usage tracking for BFS
		MemoryUsage beforeBFSMem = memoryBean.getHeapMemoryUsage();
		long startBfsTime = System.nanoTime();
		Future<Set<Node>> bfsResultFuture = runParallelBFS(executor, graph, startNode);

		// Start memory usage tracking for DFS
		MemoryUsage beforeDFSMem = memoryBean.getHeapMemoryUsage();
		long startDfsTime = System.nanoTime();
		Future<Set<Node>> dfsResultFuture = runParallelDFS(executor, graph, startNode);

		executor.shutdown();
		executor.awaitTermination(1, TimeUnit.HOURS);

		Set<Node> bfsResult = bfsResultFuture.get();
		long endBfsTime = System.nanoTime();
		MemoryUsage afterBFSMem = memoryBean.getHeapMemoryUsage();
		long bfsMemoryUsed = afterBFSMem.getUsed() - beforeBFSMem.getUsed();
		System.out.println("Parallel Graph BFS memory usage (bytes): " + bfsMemoryUsed);
		System.out.println("Graph BFS Graph Structure:");
		bfsResult.forEach(node -> printNodeAndNeighbors(graph, node));
		printExecutionTime(startBfsTime, endBfsTime);

		Set<Node> dfsResult = dfsResultFuture.get();
		long endDfsTime = System.nanoTime();
		MemoryUsage afterDFSMem = memoryBean.getHeapMemoryUsage();
		long dfsMemoryUsed = afterDFSMem.getUsed() - beforeDFSMem.getUsed();
		System.out.println("Parallel Graph DFS memory usage (bytes): " + dfsMemoryUsed);

		System.out.println("Graph DFS Graph Structure:");
		dfsResult.forEach(node -> printNodeAndNeighbors(graph, node));
		printExecutionTime(startDfsTime, endDfsTime);
	}

	private static void printNodeAndNeighbors(Graph graph, Node node) {
		System.out.print("Node " + node.getId() + " connects to: ");
		graph.getNeighbors(node).forEach(neighbor -> System.out.print(neighbor.getId() + " "));
		System.out.println();
	}

	private static void printExecutionTime(long startTime, long endTime) {
		double durationInSeconds = (endTime - startTime) / 1_000_000_000.0;
		System.out.printf("Execution time: %.9f seconds.\n", durationInSeconds);
	}

	private static Graph convertListToGraph(List<List<Integer>> adj) {
		Graph graph = new Graph();
		for (int i = 0; i < adj.size(); i++) {
			Node sourceNode = new Node(i);
			for (int j : adj.get(i)) {
				Node destinationNode = new Node(j);
				graph.addEdge(sourceNode, destinationNode);
			}
		}
		return graph;
	}

	private static Future<Set<Node>> runParallelBFS(ExecutorService executor, Graph graph, Node startNode) {
		return executor.submit(new BFSParallel(graph, startNode));
	}

	private static Future<Set<Node>> runParallelDFS(ExecutorService executor, Graph graph, Node startNode) {
		return executor.submit(new DFSParallel(graph, startNode));
	}

}