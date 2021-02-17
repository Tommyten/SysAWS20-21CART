package es.horm.cart.bin;

import es.horm.cart.bin.data.IndiaDiabetes;
import es.horm.cart.lib.CART;
import es.horm.cart.lib.RandomForest;
import es.horm.cart.lib.Util;
import es.horm.cart.lib.data.LeafData;
import es.horm.cart.lib.data.LeafDataCategorization;
import es.horm.cart.lib.data.SplitData;
import es.horm.cart.lib.metric.Gini;
import es.horm.cart.lib.tree.BinaryTree;
import es.horm.cart.lib.tree.Node;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.*;
import java.util.List;

/**
 * Hello world!
 */
public class App {

    public static void main(String[] args) {
//        new UI();
//        runIndianForestVsTree();



        List<IndiaDiabetes> diabetes = new ArrayList<>();
        diabetes.add(new IndiaDiabetes(0,0,0,0,0,0,0,0,false));
        diabetes.add(new IndiaDiabetes(0,0,0,0,0,0,0,0,true));
        diabetes.add(new IndiaDiabetes(0,0,0,0,0,0,0,0,true));
        diabetes.add(new IndiaDiabetes(0,0,0,0,0,0,0,0,true));

        CART<IndiaDiabetes> test = new CART<>(diabetes);
        Field f = test.getOutputField(IndiaDiabetes.class);
        f.setAccessible(true);
        double gini = Gini.calculateGiniForGroup(diabetes, f, List.of(false, true));
        double gini2 = Gini.calculateGiniForDataset(diabetes, new ArrayList<>(), f, List.of(false, true));
        System.out.println("Gini: " + gini);
        System.out.println("Gini2: " + gini2);

        /*List<IrisData> dataset = DataReader.readData(IrisData.class, "iris.data", ',');
        List<IrisData> testData = new ArrayList<>(dataset);
        Collections.shuffle(testData, new Random(80));
        List<IrisData> trainingData = new ArrayList<>(testData.subList(50, testData.size()));
        testData = testData.subList(0, 50);
        CART<IrisData> cart = new CART<>(trainingData);
        BinaryTree tree = cart.buildTree(5);

        RandomForest<IrisData> forest = new RandomForest<>(trainingData, 200, 75);
        forest.buildForest(true);

        HashMap<Boolean, Integer> resultTree = new HashMap<>();
        resultTree.put(false, 0);
        resultTree.put(true, 0);

        HashMap<Boolean, Integer> resultForest = new HashMap<>();
        resultForest.put(false, 0);
        resultForest.put(true, 0);

        for (IrisData iris :
                testData) {
            LeafDataCategorization leafDataTree = (LeafDataCategorization) findValue(tree, iris);
            HashMap<Comparable<?>, Double> probabilitiesTree = leafDataTree.getProbabilityMap();
            HashMap<Comparable<?>, Double> probabilitiesForest = forest.findInForest(iris);

            double treeProb = Objects.requireNonNullElse(probabilitiesTree.get(iris.getType()), 0d);
            double forestProb = Objects.requireNonNullElse(probabilitiesForest.get(iris.getType()), 0d);
            boolean temp = treeProb > 0.5d;
            resultTree.put(temp, resultTree.get(temp) + 1);

            temp = forestProb > 0.5d;
            resultForest.put(temp, resultForest.get(temp) + 1);
        }

        System.out.println(resultTree.toString());
        System.out.println(resultForest.toString());*/

    }


    public void runIndianForestVsTree() {
        List<IndiaDiabetes> dataSet = DataReader.readData(IndiaDiabetes.class, "indiaDiabetes.csv");

        List<IndiaDiabetes> testData = new ArrayList<>(dataSet);
        Collections.shuffle(testData, new Random(2));

        List<IndiaDiabetes> trainingData = new ArrayList<>(testData.subList(300, testData.size()));
        trainingData = trainingData.subList(0, 300);



        RandomForest<IndiaDiabetes> forest = new RandomForest<>(trainingData, 100);
        forest.buildForest(10);

        System.out.println("=============");

        CART<IndiaDiabetes> cart = new CART<>(trainingData);
        BinaryTree tree = cart.buildTree(10);

        HashMap<Boolean, Integer> resultTree = new HashMap<>();
        resultTree.put(false, 0);
        resultTree.put(true, 0);

        HashMap<Boolean, Integer> resultForest = new HashMap<>();
        resultForest.put(false, 0);
        resultForest.put(true, 0);

        for (IndiaDiabetes indian :
                testData) {
            LeafDataCategorization leafDataTree = (LeafDataCategorization) findValue(tree, indian);
            HashMap<Comparable<?>, Double> probabilitiesTree = leafDataTree.getProbabilityMap();
            HashMap<Comparable<?>, Double> probabilitiesForest = forest.findInForest(indian);

            double treeProb = Objects.requireNonNullElse(probabilitiesTree.get(indian.isDiabetesPositive()), 0d);
            double forestProb = Objects.requireNonNullElse(probabilitiesForest.get(indian.isDiabetesPositive()), 0d);
            boolean temp = treeProb > 0.5d;
            resultTree.put(temp, resultTree.get(temp) + 1);

            temp = forestProb > 0.5d;
            resultForest.put(temp, resultForest.get(temp) + 1);
        }

        System.out.println(resultTree.toString());
        System.out.println(resultForest.toString());
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
