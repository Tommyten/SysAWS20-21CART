package es.horm.cart.lib.metrics;

import es.horm.cart.lib.Util;

import java.lang.reflect.Field;
import java.util.List;

import static es.horm.cart.lib.Util.getFieldValueAsDouble;

public class MeanSquaredError {

    public static <T> double calculateMeanSquaredErrorFromAverage(List<T> objects, Field relevantField) {
        if(objects.size() == 0) return 0;

        double average = objects.stream()
                .mapToDouble(obj -> getFieldValueAsDouble(obj, relevantField))
                .average()
                .getAsDouble();

        return objects.stream()
                .mapToDouble(obj -> Math.pow(getFieldValueAsDouble(obj, relevantField) - average, 2))
                .average()
                .getAsDouble();
    }
}
