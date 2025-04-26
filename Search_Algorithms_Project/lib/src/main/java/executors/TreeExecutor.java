package executors;

import sequential.BFSSequential;
import sequential.DFSSequential;
import tree_entity.TreeNode;
import utils.Node;
import utils.SearchMetrics;
import utils.SearchResult;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
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
	    SearchResult result = runTreeBFSWithMetrics(fileName);
	    bfsTimes.add(result.getMetrics().getTotalTime() / 1_000_000_000.0);
	    memoryUsage.add(result.getMetrics().getMemoryUsage());
	    System.out.printf("%nSequential Tree BFS memory usage (bytes): %d%n", result.getMetrics().getMemoryUsage());
	    System.out.printf("%nTree BFS execution time (iterative): %.9f seconds.%n", 
	        result.getMetrics().getTotalTime() / 1_000_000_000.0);
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
	    SearchResult result = runTreeDFSWithMetrics(fileName);
	    dfsTimes.add(result.getMetrics().getTotalTime() / 1_000_000_000.0);
	    memoryUsage.add(result.getMetrics().getMemoryUsage());
	    System.out.printf("%nSequential Tree DFS memory usage (bytes): %d%n", result.getMetrics().getMemoryUsage());
	    System.out.printf("%nTree DFS execution time (iterative): %.9f seconds.%n", 
	        result.getMetrics().getTotalTime() / 1_000_000_000.0);
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
	    Set<Integer> childNodes = new HashSet<>();

	    try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
	        String line;
	        while ((line = reader.readLine()) != null) {
	            String[] parts = line.split(" ");
	            int nodeId = Integer.parseInt(parts[0]);
	            TreeNode node = nodes.computeIfAbsent(nodeId, TreeNode::new);

	            for (int i = 1; i < parts.length; i++) {
	                int childId = Integer.parseInt(parts[i]);
	                TreeNode child = nodes.computeIfAbsent(childId, TreeNode::new);
	                node.addChild(child);
	                childNodes.add(childId);
	            }
	        }
	    }

	    // Find root nodes (nodes that are not children)
	    Set<Integer> rootNodes = new HashSet<>(nodes.keySet());
	    rootNodes.removeAll(childNodes);

//	    System.out.println("Total nodes loaded: " + nodes.size());
//	    System.out.println("Number of root candidates: " + rootNodes.size());
	    
	    if (rootNodes.size() != 1) {
	        System.err.println("ERROR: The tree is disconnected. It contains " + 
	                         rootNodes.size() + " separate trees!");
	        System.err.println("Root candidates: " + rootNodes);
	    }

	    // Return the first root (usually node 0) or null if no roots found
	    return rootNodes.isEmpty() ? null : nodes.get(rootNodes.iterator().next());
	}
	
	public static int countAllNodes(TreeNode root) {
	    if (root == null) return 0;
	    int count = 1;
	    for (TreeNode child : root.getChildren()) {
	        count += countAllNodes(child);
	    }
	    return count;
	}
	
	/**
     * Runs both BFS and DFS on the tree and stores execution time and memory usage results in an Excel file.
     *
     * @param fileName  The file containing the tree data.
     * @param scanner   A {@code Scanner} instance for user input (not used in this method).
     * @param nodeCount The number of nodes in the tree.
     */
	public static void runTreeMethods(String fileName, Scanner scanner, int nodeCount) {
	    try {
	        SearchResult[] results = runTreeMethodsWithMetrics(fileName);
	        
	        List<Double> bfsTreeTimes = new ArrayList<>();
	        List<Double> dfsTreeTimes = new ArrayList<>();
	        List<Long> bfsTreeMemoryUsage = new ArrayList<>();
	        List<Long> dfsTreeMemoryUsage = new ArrayList<>();
	        
	        runTreeBFS(fileName, bfsTreeTimes, bfsTreeMemoryUsage);
	        runTreeDFS(fileName, dfsTreeTimes, dfsTreeMemoryUsage);
	        printComparison(results[0].getMetrics(), results[1].getMetrics());
	        
	        ExcelDataRecorder.writeData("SequentialExecutionTimes.xlsx", bfsTreeTimes, dfsTreeTimes, nodeCount, true, true);
	        ExcelDataRecorder.writeData("SequentialMemoryUsage.xlsx", convertToDoubleList(bfsTreeMemoryUsage),
	                convertToDoubleList(dfsTreeMemoryUsage), nodeCount, false, true);
	        
	        ExcelDataRecorder.writeMetricsData("TreeMetrics.xlsx", results, nodeCount);
	        
	    } catch (IOException e) {
	        LOGGER.severe("Error executing tree methods: " + e.getMessage());
	        e.printStackTrace();
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
	
	public static SearchResult runTreeBFSWithMetrics(String fileName) throws IOException {
	    long startTime = System.nanoTime();
	    MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
	    MemoryUsage beforeMem = memoryBean.getHeapMemoryUsage();
	    
	    TreeNode root = readTreeFromFile(fileName);
	    Set<Node> visitedNodes = new HashSet<>(); 
	    int nodesProcessed = 0;
	    
	    Queue<TreeNode> queue = new LinkedList<>();
	    if (root != null) {
	        queue.add(root);
	    }
	    
	    while (!queue.isEmpty()) {
	        TreeNode currentNode = queue.poll();
	        visitedNodes.add(new Node(currentNode.getValue())); 
	        nodesProcessed++;
	        
	        for (TreeNode child : currentNode.getChildren()) {
	            queue.add(child);
	        }
	    }
	    
	    long endTime = System.nanoTime();
	    MemoryUsage afterMem = memoryBean.getHeapMemoryUsage();
	    
	    return new SearchResult(
	        visitedNodes,
	        new SearchMetrics(
	            endTime - startTime,
	            endTime - startTime,
	            0,
	            afterMem.getUsed() - beforeMem.getUsed(),
	            "Tree BFS",
	            false,
	            nodesProcessed
	        )
	    );
	}

	public static SearchResult runTreeDFSWithMetrics(String fileName) throws IOException {
	    long startTime = System.nanoTime();
	    MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
	    MemoryUsage beforeMem = memoryBean.getHeapMemoryUsage();
	    
	    TreeNode root = readTreeFromFile(fileName);
	    Set<Node> visitedNodes = new HashSet<>(); 
	    int nodesProcessed = 0;
	    
	    Deque<TreeNode> stack = new ArrayDeque<>();
	    if (root != null) {
	        stack.push(root);
	    }
	    
	    while (!stack.isEmpty()) {
	        TreeNode currentNode = stack.pop();
	        if (!visitedNodes.contains(new Node(currentNode.getValue()))) {
	            visitedNodes.add(new Node(currentNode.getValue())); 
	            nodesProcessed++;
	            
	            // Push children in reverse order for DFS
	            for (int i = currentNode.getChildren().size() - 1; i >= 0; i--) {
	                stack.push(currentNode.getChildren().get(i));
	            }
	        }
	    }
	    
	    long endTime = System.nanoTime();
	    MemoryUsage afterMem = memoryBean.getHeapMemoryUsage();
	    
	    return new SearchResult(
	        visitedNodes,
	        new SearchMetrics(
	            endTime - startTime,
	            endTime - startTime,
	            0,
	            afterMem.getUsed() - beforeMem.getUsed(),
	            "Tree DFS",
	            false,
	            nodesProcessed
	        )
	    );
	}
	public static SearchResult[] runTreeMethodsWithMetrics(String fileName) throws IOException {
	    TreeNode root = readTreeFromFile(fileName);
	    int totalNodes = countAllNodes(root);
	    System.out.println("Total nodes in tree: " + totalNodes);

	    SearchResult bfsResult = runTreeBFSWithMetrics(fileName);
	    SearchResult dfsResult = runTreeDFSWithMetrics(fileName);
	    
	    bfsResult.getMetrics().printMetrics();
	    dfsResult.getMetrics().printMetrics();
	    
	    printComparison(bfsResult.getMetrics(), dfsResult.getMetrics());
	    
	    printAllNodesToFile(root, "tree_all_nodes.txt");
	    printTreeStructureToFile(fileName, "tree_structure.txt");
	    
	    return new SearchResult[]{bfsResult, dfsResult};
	}
	
	private static void printAllNodesToFile(TreeNode root, String outputFileName) throws IOException {
	    try (FileWriter writer = new FileWriter(outputFileName)) {
	        writer.write("All nodes in tree:\n");
	        Queue<TreeNode> queue = new LinkedList<>();
	        if (root != null) {
	            queue.add(root);
	        }
	        
	        while (!queue.isEmpty()) {
	            TreeNode current = queue.poll();
	            writer.write("Node " + current.getValue() + " -> ");
	            
	            for (TreeNode child : current.getChildren()) {
	                writer.write(child.getValue() + " ");
	                queue.add(child);
	            }
	            writer.write("\n");
	        }
	    }
	}
    
    public static void printTreeStructureToFile(String inputFileName, String outputFileName) throws IOException {
        TreeNode root = readTreeFromFile(inputFileName);
        try (FileWriter writer = new FileWriter(outputFileName)) {
            writer.write("Tree Structure:\n");
            printTreeStructure(root, writer, 0);
        }
    }
    
    private static void printTreeStructure(TreeNode node, FileWriter writer, int depth) throws IOException {
        if (node == null) return;
        
        // Add indentation based on depth
        for (int i = 0; i < depth; i++) {
            writer.write("  ");
        }
        
        // Print current node
        writer.write("Node " + node.getValue() + "\n");
        
        // Print all children
        for (TreeNode child : node.getChildren()) {
            printTreeStructure(child, writer, depth + 1);
        }
    }
    
    private static Set<Node> convertTreeNodesToNodes(Set<TreeNode> treeNodes) {
        Set<Node> nodes = new HashSet<>();
        for (TreeNode treeNode : treeNodes) {
            nodes.add(new Node(treeNode.getValue()));
        }
        return nodes;
    }
    
    private static void printComparison(SearchMetrics bfs, SearchMetrics dfs) {
        System.out.println("\n===== BFS vs DFS Comparison =====");
        System.out.printf("Total time ratio: %.2f (BFS/DFS)\n",
                (double) bfs.getTotalTime() / dfs.getTotalTime());

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

    
}
