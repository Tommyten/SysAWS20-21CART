package es.horm.cart.lib.data;

import java.lang.reflect.Field;

/**
 * Class which contains all data, which is relevant for the splitting of the dataset when building a decision tree.
 *
 * @param <T> Type of the Data-class which is used in the decision tree
 */
public class SplitData<T> {

    private final double metricOfSplit;
    private final Field fieldToSplitOn;
    private final T dataPointToSplitOn;
    private final Comparable<?> valueToSplitOn;

    /**
     * Initializes the SplitData object, which will contain all important values to build the decision tree and classify
     * data when the Tree is built
     * @param metricOfSplit the metric value (e.g. Gini-Index or MSE) of the split, so the resulting tree is traceable
     * @param fieldToSplitOn the field of the data-class on which the split is made
     * @param dataPointToSplitOn the exact training-data object on which the split has been made
     * @param valueToSplitOn the value on which the split has been made
     */
    public SplitData(double metricOfSplit, Field fieldToSplitOn, T dataPointToSplitOn, Comparable<?> valueToSplitOn) {
        this.metricOfSplit = metricOfSplit;
        this.fieldToSplitOn = fieldToSplitOn;
        this.dataPointToSplitOn = dataPointToSplitOn;
        this.valueToSplitOn = valueToSplitOn;
    }

    /**
     * @return the metric of the split
     */
    public double getMetricOfSplit() {
        return metricOfSplit;
    }

    /**
     * @return the field on which the split has been made
     */
    public Field getFieldToSplitOn() {
        return fieldToSplitOn;
    }

    /**
     * @return the exact datapoint of the training data on which the split has been made
     */
    public T getDataPointToSplitOn() {
        return dataPointToSplitOn;
    }

    /**
     * @return the value on which the split has been made
     */
    public Comparable<?> getValueToSplitOn() {
        return valueToSplitOn;
    }

    @Override
    public String toString() {
        return fieldToSplitOn.getName() + " < " + valueToSplitOn;
    }
}
