package es.horm.cart.example;

import es.horm.cart.example.customstrategy.BoxPlotRegressionSplitStrategy;
import es.horm.cart.example.data.BostonHousing;
import es.horm.cart.lib.CART;
import es.horm.cart.lib.Util;

import java.util.List;

public class BoxPlotUsageExample {
    public static void main(String[] args) {
        //Example for the usage of custom Strategy
        int minBucketSize = 50;
        List<BostonHousing> trainingData = DataReader.readData(BostonHousing.class, "data/boston.csv");
        CART<BostonHousing> cart = new CART<>(trainingData, BostonHousing.class);
        cart.setSplitStrategy(new BoxPlotRegressionSplitStrategy<>(minBucketSize, Util.getOutputField(BostonHousing.class)));
        cart.buildTree(minBucketSize);

        System.out.println(cart.categorize(trainingData.get(0)));
    }
}
