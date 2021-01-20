package es.horm.cart.bin;

import es.horm.cart.bin.data.GiniTestData;
import es.horm.cart.bin.data.TitanicData;
import es.horm.cart.lib.Categorization;
import es.horm.cart.lib.Regression;
import es.horm.cart.bin.data.TestData;
import es.horm.cart.lib.data.annotation.OutputField;
import es.horm.cart.lib.metrics.Gini;
import es.horm.cart.lib.tree.BinaryTree;
import es.horm.cart.lib.tree.BinaryTreePrinter;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Hello world!
 */
public class App {

    public static void main(String[] args) {
        //List<GiniTestData> dataSet = DataReader.readData(GiniTestData.class, "testDataGini.csv");
        /*List<GiniTestData> groupA = dataSet.stream().filter(giniTestData -> giniTestData.getNumber() == 0).collect(Collectors.toList());
        List<GiniTestData> groupB = dataSet.stream().filter(giniTestData -> giniTestData.getNumber() == 1).collect(Collectors.toList());
        System.out.println(dataSet);


        Field output = null;
        for (Field f :
                GiniTestData.class.getDeclaredFields()) {
            f.setAccessible(true);
            if(f.getAnnotation(OutputField.class) != null) output = f;

        }

        List<Character> values = List.of('A', 'B');

        double gini = Gini.calculateGiniForDataset(groupA, groupB, output, values);
        System.out.println(gini);*/
//        Categorization categorization = new Categorization(dataSet);

        List<GiniTestData> dataSet = DataReader.readData(GiniTestData.class, "testDataGini.csv");
        Categorization categorization = new Categorization(dataSet);
        BinaryTree tree = categorization.buildCategorizationTree();
        new BinaryTreePrinter(tree).print(System.out);


        /*List<TestData> dataSet = DataReader.readData(TestData.class, "testData.csv");
        Regression regression = new Regression(dataSet);
        BinaryTree tree = regression.buildRegressionTree();
        new BinaryTreePrinter(tree).print(System.out);*/
//        WineData wineData = new WineData(4.8f,0.65f,0.12f,1.1f,0.013f,4,10,0.99246f,3.32f,0.36f,13.5f,4);
        //findValue(tree, wineData);
    }

    /*public static void findValue(BinaryTree tree, WineData data) {
        boolean resultFound = false;
        Node currentNode = tree.getRoot();
        while(!resultFound) {
            if(currentNode.getData() instanceof LeafData) {
                resultFound = true;
                System.out.println("=======================");
                System.out.println("Result for searched WineData is: " + ((LeafData) currentNode.getData()).getOutputValue());
                System.out.println("=======================");
            } else {
                SplitData<WineData> split = (SplitData<WineData>) currentNode.getData();
                Field splitField = split.getFieldToSplitOn();
                if(Util.getFieldValue(data, splitField) < split.getValueToSplitOn())
                    currentNode = currentNode.getLeft();
                else
                    currentNode = currentNode.getRight();
            }
        }
    }*/

}
