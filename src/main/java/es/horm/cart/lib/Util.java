package es.horm.cart.lib;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;

public class Util {

    private static Logger logger = LogManager.getLogger(Regression.class);

    public static <T, R> R getFieldValue(T object, Field field) {
        try {
            return (R) field.get(object);
        } catch (IllegalAccessException e) {
            logger.error("Cannot access values of fields. Have they been flagged as accessible?");
            e.printStackTrace();
        }
        throw new RuntimeException("Something went wrong!");
    }

    public static <T> double getFieldValueAsDouble(T object, Field field) {
        try {
            return field.getDouble(object);
        } catch (IllegalAccessException e) {
            logger.error("Cannot access values of fields. Have they been flagged as accessible?");
            e.printStackTrace();
        }
        throw new RuntimeException("Something went wrong!");
    }

    public static <T> Comparable getFieldValueAsComparable(T object, Field field) {
        try {
            return (Comparable) field.get(object);
        } catch (IllegalAccessException e) {
            logger.error("Cannot access values of fields. Have they been flagged as accessible?");
            e.printStackTrace();
        }
        throw new RuntimeException("Something went wrong!");
    }
}
