package es.horm.cart.bin;

import es.horm.cart.bin.data.WineData;
import es.horm.cart.lib.CART;
import es.horm.cart.lib.tree.BinaryTree;
import es.horm.cart.lib.tree.BinaryTreePrinter;

import java.util.List;

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

        /*List<TitanicData> dataSetTitanic = DataReader.readData(TitanicData.class, "titanic.csv", ',');
        CART cart = new CART(dataSetTitanic);
        BinaryTree tree = cart.buildTree(15);
        new BinaryTreePrinter(tree).print(System.out);*/

//        List<TitanicData> dataSetTitanic = DataReader.readData(TitanicData.class, "titanic.csv", ',');
        /*Categorization categorization = new Categorization(dataSetTitanic);
        BinaryTree tree2 = categorization.buildTree(15);
        new BinaryTreePrinter(tree2).print(System.out);*/


        /*List<WineData> dataSet = DataReader.readData(WineData.class, "winequality-white.csv");
        Regression regression = new Regression(dataSet);
        BinaryTree tree = regression.buildRegressionTree(30);
        new BinaryTreePrinter(tree).print(System.out);*/

        List<WineData> dataSet = DataReader.readData(WineData.class, "winequality-white.csv");
        CART<WineData> cart = new CART<>(dataSet);
        BinaryTree tree1 = cart.buildTree(30);
        new BinaryTreePrinter(tree1).print(System.out);
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
