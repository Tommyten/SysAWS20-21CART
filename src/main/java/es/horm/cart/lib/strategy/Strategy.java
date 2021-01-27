package es.horm.cart.lib.strategy;

import es.horm.cart.lib.data.LeafData;
import es.horm.cart.lib.data.SplitData;

import java.lang.reflect.Field;
import java.util.List;

public interface Strategy<T> {

    void initStrategy(int minBucketSize, Field outputField, List<T> dataSet);

    LeafData getLeafData(List<T> data);

    double getMetric(List<T> data);

    Runnable findSplit(final Field dataColumn, T possibleSplitPoint, List<T> data, List<SplitData<T>> splitList);
}
