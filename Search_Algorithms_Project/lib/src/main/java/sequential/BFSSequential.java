package sequential;

import java.util.List;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

import tree_entity.TreeNode;

/**
 * A utility class that provides sequential implementations of Breadth-First Search (BFS)
 * for both graphs and trees.
 */
public class BFSSequential {

    // Private constructor to prevent instantiation
    private BFSSequential() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated.");
    }

    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    /**
     * Performs a BFS traversal on a graph represented as an adjacency list.
     *
     * @param adj The adjacency list representation of the graph.
     * @param s   The starting node for BFS traversal.
     */
    public static void graphBFS(List<List<Integer>> adj, int s) {
        // Validate input parameters
        if (adj == null || adj.isEmpty() || s < 0 || s >= adj.size()) {
            LOGGER.log(Level.SEVERE, "Invalid parameters for Graph BFS");
            return;
        }

        int vertexCount = adj.size();
        boolean[] visited = new boolean[vertexCount]; // Track visited nodes
        Queue<Integer> queue = new LinkedList<>(); // Queue for BFS traversal

        // Start traversal from the given source node
        visited[s] = true;
        queue.add(s);

        // Perform BFS traversal
        while (!queue.isEmpty()) {
            int u = queue.poll(); // Dequeue a node
            System.out.print(u + " "); // Process the node

            // Visit all adjacent nodes
            for (int v : adj.get(u)) {
                if (!visited[v]) {
                    visited[v] = true;
                    queue.add(v);
                }
            }
        }
    }

    /**
     * Performs a BFS traversal on a tree.
     *
     * @param root The root node of the tree.
     */
    public static void treeBFS(TreeNode root) {
        if (root == null) return;

        Queue<TreeNode> queue = new LinkedList<>(); // Queue for tree traversal
        queue.add(root);

        // Perform BFS traversal
        while (!queue.isEmpty()) {
            TreeNode currentNode = queue.poll(); // Dequeue a node
            System.out.print(currentNode.getValue() + " "); // Process the node

            // Enqueue all child nodes
            for (TreeNode child : currentNode.getChildren()) {
                queue.add(child);
            }
        }
    }
}
