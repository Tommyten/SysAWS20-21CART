package es.horm.cart;

import es.horm.cart.data.LeafData;
import es.horm.cart.data.SplitData;
import es.horm.cart.data.TestData;
import es.horm.cart.data.WineData;
import es.horm.cart.tree.BinaryTree;
import es.horm.cart.tree.Node;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Hello world!
 */
public class App {

    public static void main(String[] args) {
        List<TestData> dataSet = DataReader.readData(TestData.class, "testData.csv");
        Regression regression = new Regression(dataSet);
        BinaryTree tree = regression.buildRegressionTree();
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
