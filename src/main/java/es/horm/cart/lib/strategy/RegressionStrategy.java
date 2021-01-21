package es.horm.cart.lib.strategy;

import es.horm.cart.lib.data.SplitData;
import es.horm.cart.lib.metrics.MeanSquaredError;

import java.lang.reflect.Field;
import java.util.List;

import static es.horm.cart.lib.CART.getLeftBranch;
import static es.horm.cart.lib.CART.getRightBranch;
import static es.horm.cart.lib.Util.getFieldValueAsComparable;
import static es.horm.cart.lib.Util.getFieldValueAsDouble;

public class RegressionStrategy<T> extends Strategy<T> {

    private int minBucketSize;
    private Field outputField;

    @Override
    public void initStrategy(int minBucketSize, Field outputField, List<T> dataSet) {
        this.minBucketSize = minBucketSize;
        this.outputField = outputField;
    }

    @Override
    public Object getLeafData(List<T> data) {
        if (data.size() == 0) return Double.NaN;

        return data.stream()
                .mapToDouble(obj -> getFieldValueAsDouble(obj, outputField))
                .average()
                .getAsDouble();
    }

    @Override
    public double getMetric(List<T> data) {
        return MeanSquaredError.calculateMeanSquaredErrorFromAverage(data, outputField);
    }

    @Override
    public Runnable findSplit(Field dataColumn, T possibleSplitPoint, List<T> data, List<SplitData<T>> splitList) {
        return new Runnable() {
            @Override
            public void run() {
                Comparable splitValue = getFieldValueAsComparable(possibleSplitPoint, dataColumn);
                List<T> leftBranchCandidate = getLeftBranch(data, splitValue, dataColumn);
                if(leftBranchCandidate.size() < minBucketSize) return;

                List<T> rightBranchCandidate = getRightBranch(data, splitValue, dataColumn);
                if(rightBranchCandidate.size() <= minBucketSize) return;

                double smallerThanCandidateMSE = MeanSquaredError.calculateMeanSquaredErrorFromAverage(leftBranchCandidate, outputField);
                double greaterEqualsCandidateMSE = MeanSquaredError.calculateMeanSquaredErrorFromAverage(rightBranchCandidate, outputField);
                double totalMSE = smallerThanCandidateMSE + greaterEqualsCandidateMSE;

                SplitData<T> splitData = new SplitData<>(totalMSE, dataColumn, possibleSplitPoint, getFieldValueAsComparable(possibleSplitPoint, dataColumn));
                splitList.add(splitData);
            }
        };
    }
}
