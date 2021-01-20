package es.horm.cart.lib.tree;

public class Node {

    private Node left, right;
    private Object data;

    public Node() {
    }

    public Node(Object data) {
        this.data = data;
    }

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

    /*@Override
    public String toString() {
        StringBuilder stringbuilder = new StringBuilder();
        stringbuilder.append(data.toString())
        if(right != null)


        return super.toString();
    }*/
}
