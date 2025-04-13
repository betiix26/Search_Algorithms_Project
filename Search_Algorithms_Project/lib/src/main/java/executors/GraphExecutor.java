package executors;

import parallel.BFSParallel;
import parallel.DFSParallel;
import sequential.BFSSequential;
import sequential.DFSSequential;
import utils.Node;
import utils.SearchMetrics;
import utils.SearchResult;

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
	 * @param fileName    Path to the graph file.
	 * @param startNodeID The starting node ID for BFS traversal.
	 * @param bfsTimes    List to store BFS execution times.
	 * @param memoryUsage List to store memory usage.
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
	 * Runs sequential BFS and DFS with full metrics collection
	 * 
	 * @param fileName    Path to the graph file
	 * @param startNodeID The starting node ID
	 * @return Array of SearchResult objects [BFS result, DFS result]
	 * @throws IOException If an error occurs during file reading
	 */
	public static SearchResult[] runSequentialMethodsWithMetrics(String fileName, int startNodeID) throws IOException {
	    List<List<Integer>> adj = GraphReader.readGraph(fileName);
	    
	    // Get metrics for both algorithms
	    SearchResult bfsResult = BFSSequential.graphBFSWithMetrics(adj, startNodeID);
	    SearchResult dfsResult = DFSSequential.graphDFSWithMetrics(adj, startNodeID);
	    
	    // Print metrics
	    bfsResult.getMetrics().printMetrics();
	    dfsResult.getMetrics().printMetrics();
	    
	    // Compare results
	    printComparison(bfsResult.getMetrics(), dfsResult.getMetrics());
	    
	    return new SearchResult[]{bfsResult, dfsResult};
	}
	 
	 /**
	  * Executes DFS sequentially on a graph and records execution time and memory usage.
	  *
	  * @param fileName    Path to the graph file.
	  * @param startNodeID The starting node ID for DFS traversal.
	  * @param dfsTimes    List to store DFS execution times.
	  * @param memoryUsage List to store memory usage.
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

	     double durationInSeconds = (endTime - startTime) / 1_000_000_000.0;
	     System.out.printf("%nGraph DFS execution time (iterative): %.9f seconds.%n", durationInSeconds);
	     dfsTimes.add(durationInSeconds);
	 }

	 /**
	  * Runs BFS and DFS sequentially and saves results to Excel files
	  * 
	  * @param fileName    Path to the graph file
	  * @param startNodeID The starting node ID
	  * @param nodeCount   Number of nodes in the graph
	  */
	 public static void runSequentialMethods(String fileName, int startNodeID, int nodeCount) {
	     // Traditional measurement
	     List<Double> bfsSequentialTimes = new ArrayList<>();
	     List<Double> dfsSequentialTimes = new ArrayList<>();
	     List<Double> bfsMemoryUsage = new ArrayList<>();
	     List<Double> dfsMemoryUsage = new ArrayList<>();
	     
	     try {
	         // Run with basic measurement
	         runSequentialBFS(fileName, startNodeID, bfsSequentialTimes, bfsMemoryUsage);
	         runSequentialDFS(fileName, startNodeID, dfsSequentialTimes, dfsMemoryUsage);
	         
	         // Run with advanced metrics
	         SearchResult[] metricsResults = runSequentialMethodsWithMetrics(fileName, startNodeID);
	         
	         // Save both sets of results
	         ExcelDataRecorder.writeData("SequentialExecutionTimes.xlsx", bfsSequentialTimes, dfsSequentialTimes,
	                 nodeCount, true, false);
	         ExcelDataRecorder.writeData("SequentialMemoryUsage.xlsx", bfsMemoryUsage, dfsMemoryUsage, 
	                 nodeCount, false, false);
	         ExcelDataRecorder.writeMetricsData("SequentialDetailedMetrics.xlsx", metricsResults, nodeCount);
	         
	     } catch (Exception e) {
	         System.err.println("Error in sequential execution: " + e.getMessage());
	         e.printStackTrace();
	     }
	 }

	/**
	 * Runs BFS and DFS in parallel using an ExecutorService.
	 *
	 * @param fileName    Path to the graph file.
	 * @param startNodeID The starting node ID.
	 */
	public static SearchResult[] runParallelMethods(String fileName, int startNodeID) throws Exception {
        List<List<Integer>> adj = GraphReader.readGraph(fileName);
        Graph graph = convertListToGraph(adj);
        Node startNode = new Node(startNodeID);

        ExecutorService executor = Executors.newFixedThreadPool(2);
        
        Future<SearchResult> bfsFuture = executor.submit(new BFSParallel(graph, startNode));
        Future<SearchResult> dfsFuture = executor.submit(new DFSParallel(graph, startNode));
        
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.HOURS);
        
        SearchResult bfsResult = bfsFuture.get();
        SearchResult dfsResult = dfsFuture.get();
        
        // Print results
        bfsResult.getMetrics().printMetrics();
        dfsResult.getMetrics().printMetrics();
        
        // Compare results
        printComparison(bfsResult.getMetrics(), dfsResult.getMetrics());
        
        return new SearchResult[]{bfsResult, dfsResult};
    }

	private static void printComparison(SearchMetrics bfs, SearchMetrics dfs) {
	    System.out.println("\n===== BFS vs DFS Comparison =====");
	    System.out.printf("Total time ratio: %.2f (BFS/DFS)\n",
	            (double)bfs.getTotalTime()/dfs.getTotalTime());
	    
	    if (bfs.isParallel() || dfs.isParallel()) {
	        System.out.printf("BFS Computation/Communication ratio: %.2f\n",
	                (double) bfs.getComputationTime() / Math.max(1, bfs.getCommunicationTime()));
	        System.out.printf("DFS Computation/Communication ratio: %.2f\n",
	                (double) dfs.getComputationTime() / Math.max(1, dfs.getCommunicationTime()));

	        System.out.println("\nParallel Efficiency Analysis:");
	        System.out.printf("BFS Communication overhead: %.2f%%\n",
	                (bfs.getCommunicationTime() * 100.0 / bfs.getTotalTime()));
	        System.out.printf("DFS Communication overhead: %.2f%%\n",
	                (dfs.getCommunicationTime() * 100.0 / dfs.getTotalTime()));
	    } else {
	        System.out.println("Sequential execution - no communication overhead");
	    }
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

}