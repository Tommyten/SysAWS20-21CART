package es.horm.cart.lib.metric;

import java.lang.reflect.Field;
import java.util.List;

import static es.horm.cart.lib.Util.getFieldValueAsDouble;

/**
 * Class which provides Utility Method for calculating the MSE of a dataset
 */
public final class MeanSquaredError {

    private MeanSquaredError() {
        throw new AssertionError("Can't even instantiate this class using reflection");
    }

    /**
     * Calculates the MSE from the average of a dataset. The Field must be of Type double or auto-castable to double,
     * or else errors will occur
     * @param dataset The dataset, for which the MSE will be calculated
     * @param relevantField The field on which the calculation will take place
     * @param <T> The Type of the dataset
     * @return The MSE of the given dataset
     */
    public static <T> double calculateMeanSquaredErrorFromAverage(List<T> dataset, Field relevantField) {
        if(dataset.size() == 0) return 0;

        // calculate the average of the given Field in the dataset
        double average = dataset.stream()
                .mapToDouble(obj -> getFieldValueAsDouble(obj, relevantField))
                .average()
                .orElse(Double.NaN);

        // calculate the MSE of the dataset to the average value
        return dataset.stream()
                .mapToDouble(obj -> Math.pow(getFieldValueAsDouble(obj, relevantField) - average, 2))
                .average()
                .orElse(Double.NaN);
    }
}
