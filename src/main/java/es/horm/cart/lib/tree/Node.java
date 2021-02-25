package es.horm.cart.lib.tree;

/**
 * Basic Node class, which represents a Node of a BinaryTree
 * @see BinaryTree
 */
public class Node {

    private Node left, right;
    private Object data;

    public Node getLeft() {
        return left;
    }

    public void setLeft(Node left) {
        this.left = left;
    }

    public Node getRight() {
        return right;
    }

    public void setRight(Node right) {
        this.right = right;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
