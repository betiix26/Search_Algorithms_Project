package utils;

/**
 * Represents a node in a graph or tree with a unique identifier.
 */
public class Node {

    private final int id; // Unique identifier for the node

    /**
     * Constructor to initialize the node with an ID.
     * 
     * @param id The unique ID of the node.
     */
    public Node(int id) {
        this.id = id;
    }

    /**
     * Gets the ID of the node.
     * 
     * @return The node ID.
     */
    public int getId() {
        return id;
    }

    /**
     * Checks if two nodes are equal based on their ID.
     * 
     * @param o The object to compare with.
     * @return True if both nodes have the same ID, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return id == node.id;
    }

    /**
     * Generates a hash code based on the node ID.
     * 
     * @return The hash code of the node.
     */
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    /**
     * Returns a string representation of the node.
     * 
     * @return A string in the format "Node{id=ID}".
     */
    @Override
    public String toString() {
        return "Node{id=" + id + "}";
    }
}
