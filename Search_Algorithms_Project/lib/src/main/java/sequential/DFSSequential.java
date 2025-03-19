package sequential;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import tree_entity.TreeNode;

public class DFSSequential {
	
	private DFSSequential() {
		throw new UnsupportedOperationException("This is a utility class and cannot be instantiated.");
	}
	
    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public static void graphDFS(List<List<Integer>> adj, int s) {
        if (adj == null || adj.isEmpty() || s < 0 || s >= adj.size()) {
            LOGGER.log(Level.SEVERE, "Invalid parameters for graphDFS");
            return;
        }

        boolean[] visited = new boolean[adj.size()];
        Deque<Integer> stack = new ArrayDeque<>();

        stack.push(s);

        while (!stack.isEmpty()) {
            int u = stack.pop();
            if (!visited[u]) {
                visited[u] = true;
                System.out.print(u + " ");

                for (int i = adj.get(u).size() - 1; i >= 0; i--) {
                    int v = adj.get(u).get(i);
                    if (!visited[v]) {
                        stack.push(v);
                    }
                }
            }
        }
    }

    public static void treeDFS(TreeNode root) {
        if (root == null) {
            LOGGER.log(Level.SEVERE, "Root is null");
            return;
        }

        Deque<TreeNode> stack = new ArrayDeque<>();
        stack.push(root);

        while (!stack.isEmpty()) {
            TreeNode currentNode = stack.pop();
            System.out.print(currentNode.getValue() + " ");

            for (int i = currentNode.getChildren().size() - 1; i >= 0; i--) {
                stack.push(currentNode.getChildren().get(i));
            }
        }
    }
}
