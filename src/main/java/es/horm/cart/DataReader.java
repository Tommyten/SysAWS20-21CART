package es.horm.cart;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class DataReader {

    public static <T> List<T> readData(Class clazz, String filename) {
        List<T> data = new ArrayList<>();
        CsvMapper csvMapper = new CsvMapper();
        CsvSchema schema = CsvSchema.emptySchema().withHeader().withColumnSeparator(';');

        ObjectReader oReader = csvMapper.readerFor(clazz).with(schema);

        try (Reader reader = new FileReader(filename)) {
            MappingIterator<T> mi = oReader.readValues(reader);
            while (mi.hasNext()) {
                T current = mi.next();
                data.add(current);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return data;
    }
}
