package es.horm.cart.metrics;

import java.lang.reflect.Field;
import java.util.List;

import static es.horm.cart.Util.getFieldValue;
import static es.horm.cart.Util.getFieldValueAsDouble;

public class MeanSquaredError {

    public static <T> double calculateMeanSquaredError(List<T> objects, Field relevantField) {
        if(objects.size() == 0) return 0;

        double average = objects
                .stream()
                .mapToDouble(obj -> (double) getFieldValueAsDouble(obj, relevantField))
                .average()
                .getAsDouble();

        return objects
                .stream()
                .mapToDouble(obj -> Math.pow((double) getFieldValueAsDouble(obj, relevantField) - average, 2))
                .average()
                .getAsDouble();
    }
}
