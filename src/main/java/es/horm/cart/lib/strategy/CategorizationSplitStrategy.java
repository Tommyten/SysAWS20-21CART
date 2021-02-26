package es.horm.cart.lib.strategy;

import es.horm.cart.lib.data.LeafData;
import es.horm.cart.lib.data.LeafDataCategorization;
import es.horm.cart.lib.data.LeafDataRegression;
import es.horm.cart.lib.data.SplitData;
import es.horm.cart.lib.metric.Gini;
import es.horm.cart.lib.metric.MeanSquaredError;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static es.horm.cart.lib.Util.*;
import static es.horm.cart.lib.metric.Gini.calculateGini;

/**
 * Default Implementation of a SplitStrategy for Categorization problems.
 * Uses Gini-Index as metric for splits.
 * Uses LeafDataCategorization for the leafs
 * @param <T> The data-POJO, which represents the trainingdata and the data, which is to be classified when the tree is built
 * @see Gini
 * @see LeafDataCategorization
 */
public class CategorizationSplitStrategy<T> implements SplitStrategy<T> {

    private final int minBucketSize;
    private final Field outputField;
    private final List<?> categories;

    public CategorizationSplitStrategy(int minBucketSize, Field outputField, List<T> dataSet) {
        this.minBucketSize = minBucketSize;
        this.outputField = outputField;
        this.categories = getAllCategoriesDistinct(dataSet);
    }

    @Override
    public LeafData getLeafData(List<T> data) {
        HashMap<Comparable<?>, Integer> countMap = new HashMap<>();
        for (T t :
                data) {
            Comparable<?> comparable = getFieldValueAsComparable(t, outputField);
            if(countMap.containsKey(comparable)) {
                countMap.put(comparable, countMap.get(comparable)+1);
            } else {
                countMap.put(comparable, 1);
            }
        }
        Map<Comparable<?>, Double> leafMap = new HashMap<>();
        for (Map.Entry<Comparable<?>, Integer> entry :
                countMap.entrySet()) {
            Comparable<?> key = entry.getKey();
            Integer count = entry.getValue();
            leafMap.put(key, (double) count/data.size());
        }
        return new LeafDataCategorization(leafMap);
    }

    @Override
    public double getMetric(List<T> data) {
        return calculateGini(data, outputField, categories);
    }

    @Override
    public Runnable getSplitCalculatorTask(final Field dataColumn, T possibleSplitPoint, List<T> data, List<SplitData<T>> splitList) {
        return () -> {
            Comparable<?> splitValue = getFieldValueAsComparable(possibleSplitPoint, dataColumn);
            List<T> leftBranchCandidate = getDataSmallerThanSplitValue(data, splitValue, dataColumn);
            if (leftBranchCandidate.size() < minBucketSize) return;

            List<T> rightBranchCandidate = getDataGreaterEqualsSplitValue(data, splitValue, dataColumn);
            if (rightBranchCandidate.size() <= minBucketSize) return;

            double gini = Gini.calculateWeightedGini(leftBranchCandidate, rightBranchCandidate, outputField, categories);
            SplitData<T> splitData = new SplitData<>(gini, dataColumn, possibleSplitPoint, getFieldValueAsComparable(possibleSplitPoint, dataColumn));
            splitList.add(splitData);
        };
    }

    public List<?> getAllCategoriesDistinct(List<T> data) {
        return data.stream().map(t -> getFieldValue(t, outputField)).distinct().collect(Collectors.toList());
    }
}
