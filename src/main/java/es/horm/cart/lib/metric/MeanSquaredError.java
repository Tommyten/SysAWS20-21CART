package es.horm.cart.lib.metric;

import java.lang.reflect.Field;
import java.util.List;

import static es.horm.cart.lib.Util.getFieldValueAsDouble;

public class MeanSquaredError {

    private MeanSquaredError() {
        throw new AssertionError("Can't even instantiate this class using reflection");
    }

    public static <T> double calculateMeanSquaredErrorFromAverage(List<T> objects, Field relevantField) {
        if(objects.size() == 0) return 0;

        double average = objects.stream()
                .mapToDouble(obj -> getFieldValueAsDouble(obj, relevantField))
                .average()
                .orElse(Double.NaN); //TODO: Maybe throw exception instead?

        return objects.stream()
                .mapToDouble(obj -> Math.pow(getFieldValueAsDouble(obj, relevantField) - average, 2))
                .average()
                .orElse(Double.NaN); //TODO: Maybe throw exception instead?
    }
}
