package es.horm.cart.lib.tree;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

/**
 * Basic Binary Tree representation
 * @see Node
 */
public class BinaryTree {

    private Node root;

    /**
     * @return the root of the Tree
     */
    public Node getRoot() {
        return root;
    }

    /**
     * Sets the root of the Binary Tree to the given Node
     * @param root the Node which will become the root of the tree
     */
    public void setRoot(Node root) {
        this.root = root;
    }

    @Override
    public String toString() {
        // use the BinaryTreePrinter from Baeldung for toString()
        String data = "";

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final String utf8 = StandardCharsets.UTF_8.name();
        try (PrintStream ps = new PrintStream(baos, true, utf8)) {
            new BinaryTreePrinter(this).print(ps);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
            data = baos.toString(utf8);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return data;
    }
}
