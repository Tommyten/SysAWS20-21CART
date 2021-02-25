package es.horm.cart.example.customstrategy;

import es.horm.cart.lib.Util;
import es.horm.cart.lib.data.LeafData;
import es.horm.cart.lib.strategy.RegressionSplitStrategy;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class BoxPlotRegressionSplitStrategy<T> extends RegressionSplitStrategy<T> {

    public BoxPlotRegressionSplitStrategy(int minBucketSize, Field outputField) {
        super(minBucketSize, outputField);
    }

    @Override
    public LeafData getLeafData(List<T> data) {
        Field outputField = Util.getOutputField(data.get(0).getClass());
        data.sort(Comparator.comparingDouble(o -> Util.getFieldValueAsDouble(o, outputField)));

        List<Double> values = new ArrayList<>();
        for (T dataPoint :
                data) {
            values.add(Util.getFieldValueAsDouble(dataPoint, outputField));
        }

        double median = getMedian(values);

        List<Double> lowerThanMedian = values.stream().filter(aDouble -> aDouble < median).collect(Collectors.toList());
        List<Double> greaterThanMedian = values.stream().filter(aDouble -> aDouble > median).collect(Collectors.toList());

        double lowerQuartile = getMedian(lowerThanMedian);
        double upperQuartile = getMedian(greaterThanMedian);
        double iqr = upperQuartile - lowerQuartile;

        double maxWhiskerLength = iqr * 1.5;
        double upperWhiskerEnd = greaterThanMedian.stream()
                .filter(aDouble -> aDouble <= upperQuartile + maxWhiskerLength)
                .max(Double::compareTo)
                .orElse(Double.NaN);
        double lowerWhiskerEnd = lowerThanMedian.stream()
                .filter(aDouble -> aDouble >= lowerQuartile - maxWhiskerLength)
                .min(Double::compareTo)
                .orElse(Double.NaN);

        List<Double> runaways = values.stream()
                .filter(aDouble -> aDouble < lowerWhiskerEnd || aDouble > upperWhiskerEnd)
                .collect(Collectors.toList());


        return new BoxPlotLeaf(median, lowerQuartile, upperQuartile, upperWhiskerEnd, lowerWhiskerEnd, runaways);
    }

    private double getMedian(List<Double> values) {
        if(values.isEmpty()) return Double.NaN;
        if(values.size() == 1) return values.get(0);
        if(values.size() % 2 == 0) {
            return (values.get(values.size()/2-1) + values.get(values.size()/2))/2;
        } else {
            return values.get(values.size()/2);
        }
    }
}
