package es.horm.cart.lib.data;

/**
 * Container for the value, which lives inside the leafs of a Regression Decision Tree
 */
public class LeafDataRegression implements LeafData {

    private final double value;

    /**
     * Initializes the leaf with the given value
     * @param value the value of the leaf
     */
    public LeafDataRegression(double value) {
        this.value = value;
    }

    /**
     * @return the Leaf's value
     */
    public double getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
