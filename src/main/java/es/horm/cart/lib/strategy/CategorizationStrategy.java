package es.horm.cart.lib.strategy;

import es.horm.cart.lib.data.LeafData;
import es.horm.cart.lib.data.LeafDataCategorization;
import es.horm.cart.lib.data.SplitData;
import es.horm.cart.lib.metric.Gini;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static es.horm.cart.lib.Util.*;
import static es.horm.cart.lib.metric.Gini.calculateGiniForGroup;

public class CategorizationStrategy<T> implements Strategy<T> {

    private int minBucketSize;
    private Field outputField;
    private List<?> categories;

    @Override
    public void initStrategy(int minBucketSize, Field outputField, List<T> dataSet) {
        this.minBucketSize = minBucketSize;
        this.outputField = outputField;
        this.categories = getAllCategoriesDistinct(dataSet);
    }

    @Override
    public LeafData getLeafData(List<T> data) {
        LeafDataCategorization leafData = new LeafDataCategorization();
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
        for (Map.Entry<Comparable<?>, Integer> entry :
                countMap.entrySet()) {
            Comparable<?> key = entry.getKey();
            Integer count = entry.getValue();
            leafData.addProbability(key, (double) count/data.size());
        }
        return leafData;
    }

    @Override
    public double getMetric(List<T> data) {
        return calculateGiniForGroup(data, outputField, categories);
    }

    @Override
    public Runnable findSplit(final Field dataColumn, T possibleSplitPoint, List<T> data, List<SplitData<T>> splitList) {
        return () -> {
            Comparable<?> splitValue = getFieldValueAsComparable(possibleSplitPoint, dataColumn);
            List<T> leftBranchCandidate = getDataSmallerThanSplitValue(data, splitValue, dataColumn);
            if (leftBranchCandidate.size() < minBucketSize) return;

            List<T> rightBranchCandidate = getDataGreaterEqualsSplitValue(data, splitValue, dataColumn);
            if (rightBranchCandidate.size() <= minBucketSize) return;

            double gini = Gini.calculateGiniForDataset(leftBranchCandidate, rightBranchCandidate, outputField, categories);
            SplitData<T> splitData = new SplitData<>(gini, dataColumn, possibleSplitPoint, getFieldValueAsComparable(possibleSplitPoint, dataColumn));
            splitList.add(splitData);
        };
    }

    private List<?> getAllCategoriesDistinct(List<T> data) {
        return data.stream().map(t -> getFieldValue(t, outputField)).distinct().collect(Collectors.toList());
    }
}
