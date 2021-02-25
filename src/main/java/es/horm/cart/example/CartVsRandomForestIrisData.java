package es.horm.cart.example;

import es.horm.cart.example.data.IrisData;
import es.horm.cart.lib.CART;
import es.horm.cart.lib.RandomForest;
import es.horm.cart.lib.data.LeafData;
import es.horm.cart.lib.data.LeafDataCategorization;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartVsRandomForestIrisData {

    public static void main(String[] args) {
        List<IrisData> trainingData = DataReader.readData(IrisData.class, "data/iris.data", ',');
        CART<IrisData> cart = new CART<>(trainingData, IrisData.class);
        cart.buildTree(14);

        List<IrisData> testData = DataReader.readData(IrisData.class, "data/iris.test", ',');
        for (IrisData test :
                testData) {
            System.out.println(cart.categorize(test));
        }

        System.out.println("===================");

        RandomForest<IrisData> randomForest = new RandomForest<>(trainingData, 20_000, IrisData.class);
        randomForest.buildForest(14);

        for (IrisData test :
                testData) {
            // Kumuliere die einzelnen Ergebnisse zu einem Ergebnis, das geht bestimmt hübscher...
            List<LeafData> results = randomForest.categorize(test);
            HashMap<Comparable<?>, Double> probabilities = new HashMap<>();
            for (LeafData data :
                    results) {
                LeafDataCategorization dataCat = (LeafDataCategorization) data;
                for (Map.Entry<Comparable<?>, Double> entry:
                    dataCat.getProbabilityMap().entrySet()){
                    if(!probabilities.containsKey(entry.getKey())) {
                        probabilities.put(entry.getKey(), entry.getValue());
                    } else {
                        probabilities.put(entry.getKey(), probabilities.get(entry.getKey()) + entry.getValue());
                    }
                }
            }
            for (Map.Entry<Comparable<?>, Double> entry :
                    probabilities.entrySet()) {
                probabilities.put(entry.getKey(), entry.getValue() / results.size());
            }
            System.out.println(probabilities.toString());
        }
    }


}
