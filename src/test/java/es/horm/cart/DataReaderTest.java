package es.horm.cart;

import es.horm.cart.data.TestData;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class DataReaderTest {

    @Test
    public void shouldLoadAllData() {
        String testFileName = "testData.csv";

        List<TestData> readData = DataReader.readData(TestData.class, "testFileName");

        assertEquals(10, readData.size());
    }
}
