package es.horm.cart.lib;

import es.horm.cart.lib.annotation.InputField;
import es.horm.cart.lib.annotation.OutputField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class that contains methods, which are helpful in building decision trees using the CART algorithm.
 */
public class Util {

    private static final Logger logger = LogManager.getLogger(Util.class);

    /*
     * Private constructor to prevent this class from ever being instantiated
     */
    private Util() {
        throw new AssertionError("Can't even instantiate this class using reflection");
    }

    /**
     * Returns the Field of the class, which is marked with the @{@link es.horm.cart.lib.annotation.OutputField} annotation
     *
     * @param clazz  The class in which the output Field is to be found
     * @return The Field marked with the the @{@link es.horm.cart.lib.annotation.OutputField} annotation
     * @throws RuntimeException if no Field in the given Class is marked with the @{@link es.horm.cart.lib.annotation.OutputField} annotation
     * @see OutputField
     */
    public static Field getOutputField(Class<?> clazz) {
        Field[] fieldList = clazz.getDeclaredFields();
        List<Field> outputFields = new ArrayList<>(1);
        for (Field f :
                fieldList) {
            if (f.getAnnotation(OutputField.class) != null)
                outputFields.add(f);
        }
        if(outputFields.size() == 0)
            throw new RuntimeException("There must be exactly one OutputField");
        else if(outputFields.size() > 1)
            throw new RuntimeException("There can only be one OutputField per Data class");
        outputFields.get(0).setAccessible(true);
        return outputFields.get(0);
    }

    /**
     * Returns all Fields with the @{@link es.horm.cart.lib.annotation.InputField} annotation
     *
     * @param clazz  The class in which the data Field is to be found
     * @return a list of all fields in the specified class, which are annotated with the InputField annotation
     */
    public static List<Field> getDataFields(Class<?> clazz) {
        Field[] fieldList = clazz.getDeclaredFields();

        List<Field> inputFields = Arrays.stream(fieldList)
                .filter(field -> field.getAnnotation(InputField.class) != null)
                .collect(Collectors.toUnmodifiableList());

        // sets all fields as accessible, so reflection works even with private fields
        inputFields.forEach(field -> field.setAccessible(true));

        if(inputFields.size() == 0)
            throw new RuntimeException("There must be at least one field marked as InputField");

        return inputFields;
    }

    /**
     * Returns all Elements of the given dataset, which are smaller than the given Comparable.
     * <p>
     * This Method filters the dataset by getting the value of the given Field and then comparing it to the splitvalue for each element of the dataset.
     * If an Element's value of the given field is smaller than the splitvalue, the element will be contained in the returned List.
     * <p>
     * On the contrary, if an Element's value of the given field is greater or equals the splitvalue, the element will not be contained in the returned List.
     * To get the Elements greater/equals the split Value use {@link #getDataGreaterEqualsSplitValue(List, Comparable, Field)}
     *
     * @param dataset  The dataset, which is to be split
     * @param splitValue  The Value on which the Dataset will be Split
     * @param fielToSplitOn  The Field of the Type <i>T</i> which is used to get the value of the Elements
     * @return A list of all Elements of the dataset which are smaller than the given Comparable
     * @see #getDataGreaterEqualsSplitValue(List, Comparable, Field)
     */
    public static <T> List<T> getDataSmallerThanSplitValue(List<T> dataset, Comparable<?> splitValue, Field fielToSplitOn) {
        return dataset.stream()
                .filter(dataPoint -> getFieldValueAsComparable(dataPoint, fielToSplitOn).compareTo(splitValue) < 0)
                .collect(Collectors.toList());
    }

    /**
     * Returns all Elements of the given dataset, which are greater than or equal to the given Comparable.
     * <p>
     * This Method filters the dataset by getting the value of the given Field and then comparing it to the splitvalue for each element of the dataset.
     * If an Element's value of the given field is greater than or equal to the splitvalue, the element will be contained in the returned List.
     * <p>
     * On the contrary, if an Element's value of the given field is smaller than the splitvalue, the element will not be contained in the returned List.
     * To get the Elements greater/equals the split Value use {@link #getDataSmallerThanSplitValue(List, Comparable, Field)}
     *
     * @param dataset  The dataset, which is to be split
     * @param splitValue  The Value on which the Dataset will be Split
     * @param fielToSplitOn  The Field of the Type <i>T</i> which is used to get the value of the Elements
     * @return A list of all Elements of the dataset which are greater than or equal to the given Comparable
     * @see #getDataSmallerThanSplitValue(List, Comparable, Field)
     */
    public static <T> List<T> getDataGreaterEqualsSplitValue(List<T> dataset, Comparable<?> splitValue, Field fielToSplitOn) {
        return dataset.stream()
                .filter(dataPoint -> getFieldValueAsComparable(dataPoint, fielToSplitOn).compareTo(splitValue) >= 0)
                .collect(Collectors.toList());
    }

    /**
     * Convenience Method to get a field's value on a given object using reflection.
     * <p>
     * Needs the field to be set as accessible.
     *
     * @param object  The object of which the field should be read
     * @param field  The field which will be read
     * @param <T>  The Type of the Object
     * @param <R>  The type of the return value
     * @return the value of the field on the given object
     */
    public static <T, R> R getFieldValue(T object, Field field) {
        field.setAccessible(true);
        try {
            return (R) field.get(object);
        } catch (IllegalAccessException e) {
            logger.error("Cannot access values of fields. Have they been flagged as accessible?");
            e.printStackTrace();
        }
        throw new RuntimeException("Something went wrong!");
    }

    /**
     * Convenience Method to get a field's value on a given object using reflection.
     * <p>
     * Needs the field to be set as accessible.
     *
     * @param object  The object of which the field should be read
     * @param field  The field which will be read
     * @param <T>  The Type of the Object
     * @return the value of the field on the given object as double
     */
    public static <T> double getFieldValueAsDouble(T object, Field field) {
        field.setAccessible(true);
        try {
            return field.getDouble(object);
        } catch (IllegalAccessException e) {
            logger.error("Cannot access values of fields. Have they been flagged as accessible?");
            e.printStackTrace();
        }
        throw new RuntimeException("Something went wrong!");
    }

    /**
     * Convenience Method to get a field's value on a given object using reflection.
     * <p>
     * Needs the field to be set as accessible.
     *
     * @param object  The object of which the field should be read
     * @param field  The field which will be read
     * @param <T>  The Type of the Object
     * @param <R>  The Type of Objects the Comparable may be compared to
     * @return the value of the field on the given object as Comparable
     */
    public static <T, R> Comparable<R> getFieldValueAsComparable(T object, Field field) {
        field.setAccessible(true);
        try {
            return (Comparable<R>) field.get(object);
        } catch (IllegalAccessException e) {
            logger.error("Cannot access values of fields. Have they been flagged as accessible?");
            e.printStackTrace();
        }
        throw new RuntimeException("Something went wrong!");
    }
}
