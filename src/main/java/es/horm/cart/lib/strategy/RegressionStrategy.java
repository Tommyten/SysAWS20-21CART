package es.horm.cart.lib.strategy;

import es.horm.cart.lib.data.LeafData;
import es.horm.cart.lib.data.LeafDataRegression;
import es.horm.cart.lib.data.SplitData;
import es.horm.cart.lib.metric.MeanSquaredError;

import java.lang.reflect.Field;
import java.util.List;

import static es.horm.cart.lib.Util.*;

public class RegressionStrategy<T> implements Strategy<T> {

    private int minBucketSize;
    private Field outputField;

    @Override
    public void initStrategy(int minBucketSize, Field outputField, List<T> dataSet) {
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
    public Runnable findSplit(Field dataColumn, T possibleSplitPoint, List<T> data, List<SplitData<T>> splitList) {
        return () -> {
            Comparable<?> splitValue = getFieldValueAsComparable(possibleSplitPoint, dataColumn);
            List<T> leftBranchCandidate = getDataSmallerThanSplitValue(data, splitValue, dataColumn);
            if(leftBranchCandidate.size() < minBucketSize) return;

            List<T> rightBranchCandidate = getDataGreaterEqualsSplitValue(data, splitValue, dataColumn);
            if(rightBranchCandidate.size() <= minBucketSize) return;

            double smallerThanCandidateMSE = MeanSquaredError.calculateMeanSquaredErrorFromAverage(leftBranchCandidate, outputField);
            double greaterEqualsCandidateMSE = MeanSquaredError.calculateMeanSquaredErrorFromAverage(rightBranchCandidate, outputField);
            double totalMSE = smallerThanCandidateMSE + greaterEqualsCandidateMSE;

            SplitData<T> splitData = new SplitData<>(totalMSE, dataColumn, possibleSplitPoint, getFieldValueAsComparable(possibleSplitPoint, dataColumn));
            splitList.add(splitData);
        };
    }
}
