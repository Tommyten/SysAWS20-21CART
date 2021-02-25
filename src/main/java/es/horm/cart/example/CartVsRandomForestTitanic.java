package es.horm.cart.example;

import es.horm.cart.example.data.TitanicData;
import es.horm.cart.lib.CART;
import es.horm.cart.lib.RandomForest;
import es.horm.cart.lib.data.LeafData;
import es.horm.cart.lib.data.LeafDataCategorization;

import java.util.*;

public class CartVsRandomForestTitanic {

    static final int AMOUNT_OF_TESTDATA = 80;

    public static void main(String[] args) {
        // classifies 80 instances of the titanic dataset using CART and RandomForests then prints how many it got correct
        List<TitanicData> titanicData = DataReader.readData(TitanicData.class, "data/titanic.csv", ',');

        Collections.shuffle(titanicData, new Random(1));
        List<TitanicData> testData = titanicData.subList(0, AMOUNT_OF_TESTDATA);
        List<TitanicData> trainingData = titanicData.subList(AMOUNT_OF_TESTDATA, titanicData.size());

        CART<TitanicData> cart = new CART<>(trainingData, TitanicData.class);
        cart.buildTree(50);

        int correctCart = 0;
        for (TitanicData test :
                testData) {
            LeafDataCategorization leaf = (LeafDataCategorization) cart.categorize(test);
            double survivalProbability = leaf.getProbability(test.isSurvived());
            if(survivalProbability > 0.667) correctCart++;
        }
        System.out.println("CART classified " + correctCart + " out of " + AMOUNT_OF_TESTDATA + " correctly!");

        RandomForest<TitanicData> randomForest = new RandomForest<>(trainingData, 1_000, TitanicData.class);
        randomForest.buildForest(50);

        int correctRandomForest = 0;
        for (TitanicData test :
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
            if(probabilities.get(test.isSurvived()) > 0.667) correctRandomForest++;
        }
        System.out.println("RandomForest classified " + correctRandomForest + " out of " + AMOUNT_OF_TESTDATA + " correctly!");
    }
}
