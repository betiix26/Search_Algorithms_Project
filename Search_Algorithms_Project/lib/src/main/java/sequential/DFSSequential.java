package sequential;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import tree_entity.TreeNode;

/**
 * A utility class that provides sequential implementations of Depth-First Search (DFS)
 * for both graphs and trees.
 */
public class DFSSequential {

    // Private constructor to prevent instantiation
    private DFSSequential() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated.");
    }

    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    /**
     * Performs an iterative DFS traversal on a graph represented as an adjacency list.
     *
     * @param adj The adjacency list representation of the graph.
     * @param s   The starting node for DFS traversal.
     */
    public static void graphDFS(List<List<Integer>> adj, int s) {
        // Validate input parameters
        if (adj == null || adj.isEmpty() || s < 0 || s >= adj.size()) {
            LOGGER.log(Level.SEVERE, "Invalid parameters for Graph DFS");
            return;
        }

        boolean[] visited = new boolean[adj.size()]; // Track visited nodes
        Deque<Integer> stack = new ArrayDeque<>(); // Stack for DFS traversal

        // Start DFS traversal from the given source node
        stack.push(s);

        while (!stack.isEmpty()) {
            int u = stack.pop(); // Pop the top node

            if (!visited[u]) {
                visited[u] = true;
                System.out.print(u + " "); // Process the node

                // Push adjacent nodes in reverse order to maintain expected traversal order
                for (int i = adj.get(u).size() - 1; i >= 0; i--) {
                    int v = adj.get(u).get(i);
                    if (!visited[v]) {
                        stack.push(v);
                    }
                }
            }
        }
    }

    /**
     * Performs an iterative DFS traversal on a tree.
     *
     * @param root The root node of the tree.
     */
    public static void treeDFS(TreeNode root) {
        if (root == null) {
            LOGGER.log(Level.SEVERE, "Root is null");
            return;
        }

        Deque<TreeNode> stack = new ArrayDeque<>(); // Stack for DFS traversal
        stack.push(root);

        while (!stack.isEmpty()) {
            TreeNode currentNode = stack.pop(); // Pop the top node
            System.out.print(currentNode.getValue() + " "); // Process the node

            // Push children in reverse order to maintain left-to-right traversal order
            for (int i = currentNode.getChildren().size() - 1; i >= 0; i--) {
                stack.push(currentNode.getChildren().get(i));
            }
        }
    }
}
