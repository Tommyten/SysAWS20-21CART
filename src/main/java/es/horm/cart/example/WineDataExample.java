package es.horm.cart.example;

import es.horm.cart.example.data.WineData;
import es.horm.cart.lib.CART;
import es.horm.cart.lib.RandomForest;
import es.horm.cart.lib.data.LeafData;
import es.horm.cart.lib.data.LeafDataRegression;

import java.util.List;
import java.util.stream.Collectors;

public class WineDataExample {

    // ACHTUNG!!
    // Ausführung kann bei hoher Anzahl zu generierender Bäume im Wald SEHR lange dauern und ist SEHR rechenaufwändig!
    public static final int TREES_IN_FOREST = 100;

    public static void main(String[] args) {
        // Klassifiziert alle Elemente, welche eine Bewertung von 9 haben und gibt den durchschnittlichen Fehler für CART und Random Forest an
        List<WineData> trainingData = DataReader.readData(WineData.class, "data/winequality-white.csv");
        List<WineData> testData = trainingData.stream().filter(data -> data.getQuality() == 9).collect(Collectors.toList());

        CART<WineData> cart = new CART<>(trainingData, WineData.class);
        cart.buildTree(10);

        double totalErrorCart = 0;
        for (WineData data :
                testData) {
            LeafDataRegression leaf = (LeafDataRegression) cart.categorize(data);
            totalErrorCart += 9- leaf.getValue();
        }
        System.out.println("Average error of Tree: " + totalErrorCart / testData.size());

        RandomForest<WineData> randomForest = new RandomForest<>(trainingData, TREES_IN_FOREST, WineData.class);
        randomForest.buildForest(10);
        double totalErrorForest = 0;
        for (WineData data :
                testData) {
            List<LeafData> categorization = randomForest.categorize(data);
            List<LeafDataRegression> regressionClassificationData = categorization.stream()
                    .map(leafData -> (LeafDataRegression) leafData)
                    .collect(Collectors.toList());

            double averageForestResult = regressionClassificationData.stream()
                    .mapToDouble(LeafDataRegression::getValue)
                    .average()
                    .getAsDouble();
            totalErrorForest += 9-averageForestResult;
        }
        System.out.println("Average error of Forest: " + totalErrorForest / testData.size());
    }
}
