package es.horm.cart;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;

public class Util {

    private static Logger logger = LogManager.getLogger(Regression.class);

    public static <T> double getFieldValue(T object, Field field) {
        try {
            return field.getDouble(object);
        } catch (IllegalAccessException e) {
            logger.error("Cannot access values of fields. Have they been flagged as accessible? Will return NaN!");
            e.printStackTrace();
        } catch (NullPointerException e) {
            logger.debug("Field was null, returning NaN");
        }
        return Double.NaN;
    }
}
