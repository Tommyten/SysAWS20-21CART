package es.horm.cart.lib.strategy;

import es.horm.cart.lib.data.SplitData;

import java.lang.reflect.Field;
import java.util.List;

public abstract class Strategy<T> {

    public abstract void initStrategy(int minBucketSize, Field outputField, List<T> dataSet);

    public abstract Object getLeafData(List<T> data);

    public abstract double getMetric(List<T> data);

    public abstract Runnable findSplit(final Field dataColumn, T possibleSplitPoint, List<T> data, List<SplitData<T>> splitList);
}
