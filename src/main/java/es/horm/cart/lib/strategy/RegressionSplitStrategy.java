package es.horm.cart.lib.strategy;

import es.horm.cart.lib.data.LeafData;
import es.horm.cart.lib.data.LeafDataRegression;
import es.horm.cart.lib.data.SplitData;
import es.horm.cart.lib.metric.MeanSquaredError;

import java.lang.reflect.Field;
import java.util.List;

import static es.horm.cart.lib.Util.*;

/**
 * Default Implementation of a SplitStrategy for Regression problems.
 * Uses Mean Squared error as metric for splits.
 * Uses LeafDataRegression for the leafs
 * @param <T> The data-POJO, which represents the trainingdata and the data, which is to be classified when the tree is built
 * @see MeanSquaredError
 * @see LeafDataRegression
 */
public class RegressionSplitStrategy<T> implements SplitStrategy<T> {

    private final int minBucketSize;
    private final Field outputField;

    public RegressionSplitStrategy(int minBucketSize, Field outputField) {
        this.minBucketSize = minBucketSize;
        this.outputField = outputField;
    }

    @Override
    public LeafData getLeafData(List<T> data) {

        if (data.size() == 0) return new LeafDataRegression(Double.NaN);

        double average = data.stream()
                .mapToDouble(obj -> getFieldValueAsDouble(obj, outputField))
                .average()
                .orElse(Double.NaN);

        return new LeafDataRegression(average);
    }

    @Override
    public double getMetric(List<T> data) {
        return MeanSquaredError.calculateMeanSquaredErrorFromAverage(data, outputField);
    }

    @Override
    public Runnable getSplitCalculatorTask(Field dataColumn, T possibleSplitPoint, List<T> data, List<SplitData<T>> splitList) {
        return () -> {
            Comparable<?> splitValue = getFieldValueAsComparable(possibleSplitPoint, dataColumn);
            List<T> leftBranchCandidate = getDataSmallerThanSplitValue(data, splitValue, dataColumn);
            if(leftBranchCandidate.size() < minBucketSize) return;

            List<T> rightBranchCandidate = getDataGreaterEqualsSplitValue(data, splitValue, dataColumn);
            if(rightBranchCandidate.size() <= minBucketSize) return;

            double smallerThanCandidateMSE = getMetric(leftBranchCandidate);
            double greaterEqualsCandidateMSE = getMetric(rightBranchCandidate);
            double totalMSE = (double) leftBranchCandidate.size()/data.size() * smallerThanCandidateMSE +
                    (double) rightBranchCandidate.size()/data.size() * greaterEqualsCandidateMSE;

            SplitData<T> splitData = new SplitData<>(totalMSE, dataColumn, possibleSplitPoint, getFieldValueAsComparable(possibleSplitPoint, dataColumn));
            splitList.add(splitData);
        };
    }
}
