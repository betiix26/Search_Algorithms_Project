package tree_entity;

import java.util.ArrayList;
import java.util.List;

public class TreeNode {

	private int value;
	private List<TreeNode> children;

	public TreeNode(int value) {
		this.value = value;
		this.children = new ArrayList<>();
	}

	public void addChild(TreeNode child) {
		this.children.add(child);
	}

	public int getValue() {
		return this.value;
	}

	public List<TreeNode> getChildren() {
		return this.children;
	}
}
