package es.horm.cart.bin;

import es.horm.cart.bin.data.IndiaDiabetes;
import es.horm.cart.lib.CART;
import es.horm.cart.lib.RandomForest;
import es.horm.cart.lib.Util;
import es.horm.cart.lib.data.LeafData;
import es.horm.cart.lib.data.LeafDataCategorization;
import es.horm.cart.lib.data.SplitData;
import es.horm.cart.lib.tree.BinaryTree;
import es.horm.cart.lib.tree.BinaryTreePrinter;
import es.horm.cart.lib.tree.Node;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Hello world!
 */
public class App {

    public static void main(String[] args) {

//        IndiaDiabetes indian = new IndiaDiabetes(5,121,72,23,112,26.2f,0.245f,30,false);

        List<IndiaDiabetes> dataSet = DataReader.readData(IndiaDiabetes.class, "indiaDiabetes.csv");
        RandomForest<IndiaDiabetes> forest = new RandomForest<>(dataSet, 25, 75);
        forest.buildForest(true);
//        forest.doThings(dataSet, 25, 75);
//        System.out.println(forest.findInForest(indian).toString());

        System.out.println("=============");

        CART<IndiaDiabetes> cart = new CART<>(dataSet);
        BinaryTree tree = cart.buildTree(15);
//        findValue(tree, indian);

        HashMap<Boolean, Integer> resultTree = new HashMap<>();
        resultTree.put(false, 0);
        resultTree.put(true, 0);

        HashMap<Boolean, Integer> resultForest = new HashMap<>();
        resultForest.put(false, 0);
        resultForest.put(true, 0);

        for (IndiaDiabetes indian :
                dataSet) {
            LeafDataCategorization leafDataTree = (LeafDataCategorization) findValue(tree, indian);
            HashMap<Comparable<?>, Double> probabilitiesTree = leafDataTree.getProbabilityMap();
            HashMap<Comparable<?>, Double> probabilitiesForest = forest.findInForest(indian);

            double treeProb = probabilitiesTree.get(indian.isDiabetesPositive());
            double forestProb = probabilitiesForest.get(indian.isDiabetesPositive());
            boolean temp = treeProb > 0.5d;
            resultTree.put(temp, resultTree.get(temp) + 1);

            temp = forestProb > 0.5d;
            resultForest.put(temp, resultForest.get(temp) + 1);
        }

        System.out.println(resultTree.toString());
        System.out.println(resultForest.toString());











        /*List<IndiaDiabetes> dataSet = DataReader.readData(IndiaDiabetes.class, "indiaDiabetes.csv");
        CART<IndiaDiabetes> cart = new CART<>(dataSet);
        BinaryTree tree = cart.buildTree(15);
        new BinaryTreePrinter(tree).print(System.out);*/
/*
        List<Boolean> predictions = new ArrayList<>();

        for (IndiaDiabetes id :
                dataSet) {
            LeafDataCategorization data = (LeafDataCategorization) findValue(tree, id);
            List<Boolean> dataList = (List<Boolean>) data.getOutput();
            long trues = dataList.stream().filter(aBoolean -> aBoolean).count();
            long falses = dataList.stream().filter(aBoolean -> !aBoolean).count();
            double truePercent = (double) trues/dataList.size();
            double falsePercent = (double) falses/dataList.size();

            boolean wasPredictionCorrect = false;
            if(truePercent >= 0.75 && id.isDiabetesPositive()) wasPredictionCorrect = true;
            if(falsePercent >= 0.75 && !id.isDiabetesPositive()) wasPredictionCorrect = true;
            predictions.add(wasPredictionCorrect);
        }


        long correctPredictions = predictions.stream().filter(aBoolean -> aBoolean).count();
        long falsePredictions = predictions.stream().filter(aBoolean -> !aBoolean).count();
        double correctPercent = (double) correctPredictions/predictions.size()*100;
        double falsePercent = (double) falsePredictions/predictions.size()*100;
        System.out.println("Of " + dataSet.size() + " cases " + correctPredictions + " -> " + String.format("%.2f", correctPercent) + "% were predicted correctly!");
        System.out.println("Of " + dataSet.size() + " cases " + falsePredictions + " -> " + String.format("%.2f", falsePercent) + "% were predicted incorrectly!");*/

    }

    public static <T> LeafData findValue(BinaryTree tree, T data) {
        boolean resultFound = false;
        Node currentNode = tree.getRoot();
        while(!resultFound) {
            if(currentNode.getData() instanceof LeafData) {
                resultFound = true;
//                System.out.println("Result for searched Data is: " + currentNode.getData().toString());
                return (LeafData) currentNode.getData();
            } else {
                SplitData<T> split = (SplitData<T>) currentNode.getData();
                Field splitField = split.getFieldToSplitOn();
                if(Util.getFieldValueAsComparable(data, splitField).compareTo(split.getValueToSplitOn()) < 0)
                    currentNode = currentNode.getLeft();
                else
                    currentNode = currentNode.getRight();
            }
        }
        return null;
    }

}
