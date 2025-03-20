package tree_entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a node in a tree data structure.
 * Each node contains an integer value and a list of child nodes.
 */
public class TreeNode {

    private final int value; // Value of the node
    private final List<TreeNode> children; // List of child nodes

    /**
     * Constructs a TreeNode with the specified value.
     *
     * @param value The value of the node.
     */
    public TreeNode(int value) {
        this.value = value;
        this.children = new ArrayList<>();
    }

    /**
     * Adds a child node to the current node.
     *
     * @param child The child node to be added.
     */
    public void addChild(TreeNode child) {
        this.children.add(child);
    }

    /**
     * Returns the value of the node.
     *
     * @return The integer value of this node.
     */
    public int getValue() {
        return this.value;
    }

    /**
     * Returns the list of child nodes.
     *
     * @return A list containing the child nodes of this TreeNode.
     */
    public List<TreeNode> getChildren() {
        return this.children;
    }
}
