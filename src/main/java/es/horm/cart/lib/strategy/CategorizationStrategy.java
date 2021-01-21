package es.horm.cart.lib.strategy;

import es.horm.cart.lib.CART;
import es.horm.cart.lib.data.SplitData;
import es.horm.cart.lib.metrics.Gini;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

import static es.horm.cart.lib.Util.getFieldValue;
import static es.horm.cart.lib.Util.getFieldValueAsComparable;

public class CategorizationStrategy<T> extends Strategy<T> {

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
    public Object getLeafData(List<T> data) {
        return data.stream().map(t -> getFieldValue(t, outputField)).collect(Collectors.toList());
    }

    @Override
    public double getMetric(List<T> data) {
        return Gini.calculateGiniForGroup(data, outputField, categories);
    }

    @Override
    public Runnable findSplit(final Field dataColumn, T possibleSplitPoint, List<T> data, List<SplitData<T>> splitList) {
        return new Runnable() {
            @Override
            public void run() {
                Comparable splitValue = getFieldValueAsComparable(possibleSplitPoint, dataColumn);
                List<T> leftBranchCandidate = CART.getLeftBranch(data, splitValue, dataColumn);
                if (leftBranchCandidate.size() < minBucketSize) return;

                List<T> rightBranchCandidate = CART.getRightBranch(data, splitValue, dataColumn);
                if (rightBranchCandidate.size() <= minBucketSize) return;

                double gini = Gini.calculateGiniForDataset(leftBranchCandidate, rightBranchCandidate, outputField, categories);
                SplitData<T> splitData = new SplitData<>(gini, dataColumn, possibleSplitPoint, getFieldValueAsComparable(possibleSplitPoint, dataColumn));
                splitList.add(splitData);
            }
        };
    }

    private List<?> getAllCategoriesDistinct(List<T> data) {
        return data.stream().map(t -> getFieldValue(t, outputField)).distinct().collect(Collectors.toList());
    }
}
