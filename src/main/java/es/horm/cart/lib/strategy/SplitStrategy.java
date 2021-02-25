package es.horm.cart.lib.strategy;

import es.horm.cart.lib.Util;
import es.horm.cart.lib.data.LeafData;
import es.horm.cart.lib.data.SplitData;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * This Interface must be implemented by all Split Strategies.
 * Is used as type for all Split Strategies, by the CART class and used to make the splits in the decision tree
 * depending on the specific implementation of the split strategy.
 * @param <T> The data-POJO, which represents the trainingdata and the data, which is to be classified when the tree is built
 */
public interface SplitStrategy<T> {

    /**
     * Builds the Leaf, which contain the given data
     * @param data the data for which the leaf is to be built
     * @return the leaf
     */
    LeafData getLeafData(List<T> data);

    /**
     * returns the Split-Metric for a given dataset.
     * A Metric of 0 is considered perfect, the higher the worse the metric is.
     * @param data the dataset for which the metric is to be calculated
     * @return the metric value
     */
    double getMetric(List<T> data);

    /**
     * Returns a runnable, which calculates a split and then adds the corresponding SplitData Object to a given list
     * @param dataColumn the field for which the split is to be calculated
     * @param possibleSplitPoint the datapoint on which the split will be made
     * @param data the complete dataset
     * @param splitList the list to which the calculated SplitData will be added
     * @return a runnable, which executes the calculation
     */
    Runnable getSplitCalculatorTask(final Field dataColumn, T possibleSplitPoint, List<T> data, List<SplitData<T>> splitList);

    /**
     * Executes multiple Split-Calculator Tasks in parallel using a cached thread pool.
     * After all Splits have been calculated a List of all possible Splits is returned.
     * @param dataSet The data for which all splits are to be calculated
     * @return a list of all possible Splits
     */
    default List<SplitData<T>> calculateAllSplitsForDataSet(List<T> dataSet) {
        List<SplitData<T>> splitList = Collections.synchronizedList(new ArrayList<>());
        List<Field> dataFields = Util.getDataFields(dataSet.get(0).getClass());

        ExecutorService es = Executors.newCachedThreadPool();

        for (Field dataColumn :
                dataFields) {
            for (T possibleSplitPoint :
                    dataSet) {
                es.execute(getSplitCalculatorTask(dataColumn, possibleSplitPoint, dataSet, splitList));
            }
        }

        es.shutdown();
        boolean finished = false;
        while (!finished) {
            try {
                finished = es.awaitTermination(1, TimeUnit.MINUTES);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return splitList;
    }
}
