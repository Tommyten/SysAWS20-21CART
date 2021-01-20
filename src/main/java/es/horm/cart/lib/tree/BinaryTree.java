package es.horm.cart.lib.tree;

public class BinaryTree {

    private Node root;

    public BinaryTree() {
    }

    public BinaryTree(Node root) {
        this.root = root;
    }

    public Node getRoot() {
        return root;
    }

    public void setRoot(Node root) {
        this.root = root;
    }

    /*@Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        Node currentNode = root;
        currentNode.getData()


        return super.toString();
    }*/
}
