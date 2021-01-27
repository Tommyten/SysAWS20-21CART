package es.horm.cart.lib.data;

public class LeafDataRegression implements LeafData {

    private double value;

    public LeafDataRegression() {
    }

    public LeafDataRegression(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
