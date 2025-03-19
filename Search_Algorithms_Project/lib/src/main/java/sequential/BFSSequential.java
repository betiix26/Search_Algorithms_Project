package sequential;

import java.util.List;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

import tree_entity.TreeNode;

public class BFSSequential {
	
	private BFSSequential() {
		throw new UnsupportedOperationException("This is a utility class and cannot be instantiated.");
	}
	
    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public static void graphBFS(List<List<Integer>> adj, int s) {
        if (adj == null || adj.isEmpty() || s < 0 || s >= adj.size()) {
            LOGGER.log(Level.SEVERE, "Invalid parameters for graphBFS");
            return;
        }

        int V = adj.size();
        boolean[] visited = new boolean[V];
        Queue<Integer> queue = new LinkedList<>();

        visited[s] = true;
        queue.add(s);

        while (!queue.isEmpty()) {
            int u = queue.poll();
            System.out.print(u + " ");

            for (int v : adj.get(u)) {
                if (!visited[v]) {
                    visited[v] = true;
                    queue.add(v);
                }
            }
        }
    }

    public static void treeBFS(TreeNode root) {
        if (root == null) return;

        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(root);

        while (!queue.isEmpty()) {
            TreeNode currentNode = queue.poll();
            System.out.print(currentNode.getValue() + " "); 

            for (TreeNode child : currentNode.getChildren()) { 
                queue.add(child);
            }
        }
    }
}
