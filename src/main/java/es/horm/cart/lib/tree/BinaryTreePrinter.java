package es.horm.cart.lib.tree;

import java.io.PrintStream;

/**
 * Source:
 * https://www.baeldung.com/java-print-binary-tree-diagram
 */
public class BinaryTreePrinter {

    private final BinaryTree tree;

    public BinaryTreePrinter(BinaryTree tree) {
        this.tree = tree;
    }

    private String traversePreOrder(BinaryTree root) {

        if (root == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        sb.append(root.getRoot().getData());

        String pointerRight = "└──";
        String pointerLeft = (root.getRoot().getRight() != null) ? "├──" : "└──";

        traverseNodes(sb, "", pointerLeft, root.getRoot().getLeft(), root.getRoot().getRight() != null);
        traverseNodes(sb, "", pointerRight, root.getRoot().getRight(), false);

        return sb.toString();
    }

    private void traverseNodes(StringBuilder sb, String padding, String pointer, Node node,
                               boolean hasRightSibling) {

        if (node != null) {

            sb.append("\n");
            sb.append(padding);
            sb.append(pointer);
            sb.append(node.getData());

            StringBuilder paddingBuilder = new StringBuilder(padding);
            if (hasRightSibling) {
                paddingBuilder.append("│  ");
            } else {
                paddingBuilder.append("   ");
            }

            String paddingForBoth = paddingBuilder.toString();
            String pointerRight = "└──";
            String pointerLeft = (node.getRight() != null) ? "├──" : "└──";

            traverseNodes(sb, paddingForBoth, pointerLeft, node.getLeft(), node.getRight() != null);
            traverseNodes(sb, paddingForBoth, pointerRight, node.getRight(), false);

        }

    }

    public void print(PrintStream os) {
        os.print(traversePreOrder(tree));
        os.print("\n");
    }

}