package es.horm.cart.data;

import java.lang.reflect.Field;

public class SplitData<T> {
    private final double mseOfSplit;
    private final Field fieldToSplitOn;
    private final T dataPointToSplitOn;
    private final Comparable valueToSplitOn;

    public SplitData(double mseOfSplit, Field fieldToSplitOn, T dataPointToSplitOn, Comparable valueToSplitOn) {
        this.mseOfSplit = mseOfSplit;
        this.fieldToSplitOn = fieldToSplitOn;
        this.dataPointToSplitOn = dataPointToSplitOn;
        this.valueToSplitOn = valueToSplitOn;
    }

    public double getMseOfSplit() {
        return mseOfSplit;
    }

    public Field getFieldToSplitOn() {
        return fieldToSplitOn;
    }

    public T getDataPointToSplitOn() {
        return dataPointToSplitOn;
    }

    public Comparable getValueToSplitOn() {
        return valueToSplitOn;
    }

    @Override
    public String toString() {
        return "SplitData{" +
                "mseOfSplit=" + mseOfSplit +
                ", fieldToSplitOn=" + fieldToSplitOn +
                ", dataPointToSplitOn=" + dataPointToSplitOn +
                ", valueToSplitOn=" + valueToSplitOn +
                '}';
    }
}
