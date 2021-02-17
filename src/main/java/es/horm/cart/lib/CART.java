package es.horm.cart.lib;

import es.horm.cart.lib.data.LeafData;
import es.horm.cart.lib.data.SplitData;
import es.horm.cart.lib.annotation.OutputField;
import es.horm.cart.lib.strategy.CategorizationStrategy;
import es.horm.cart.lib.strategy.RegressionStrategy;
import es.horm.cart.lib.strategy.Strategy;
import es.horm.cart.lib.tree.BinaryTree;
import es.horm.cart.lib.tree.Node;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static es.horm.cart.lib.Util.*;

public class CART<T> {

    private static final Logger logger = LogManager.getLogger(CART.class);

    private Strategy<T> strategy;

    private final Field outputField;
    private final List<Field> dataFields;
    private final List<T> dataSet;

    private final BinaryTree tree;

    private int minBucketSize;

    public CART(List<T> dataSet) {
        this.dataSet = dataSet;
        outputField = getOutputField(dataSet.get(0).getClass());
        dataFields = getDataFields(dataSet.get(0).getClass());
        setAllFieldsAccessible();

        initStrategy();

        tree = new BinaryTree();
        tree.setRoot(new Node());
    }

    public CART(List<T> dataSet, List<Field> allowedFields) {
        this.dataSet = dataSet;
        outputField = getOutputField(dataSet.get(0).getClass());
        dataFields = allowedFields;
        setAllFieldsAccessible();

        initStrategy();

        tree = new BinaryTree();
        tree.setRoot(new Node());
    }

    private void initStrategy() {
        // Sets the Strategy to be used
        if (outputField.getType().equals(double.class) ||
                outputField.getType().equals(Double.class) ||
                outputField.getType().equals(Float.class) ||
                outputField.getType().equals(float.class)) {
            // If the output field is of Type Double or Float, assume a regression Problem
            logger.info("Assuming a Regression Problem");
            strategy = new RegressionStrategy<>();
        } else {
            logger.info("Assuming a Categorization Problem");
            strategy = new CategorizationStrategy<>();
        }
    }

    /**
     * Sets all Fields as accessible
     * important as without it reflection cannot work!
     */
    private void setAllFieldsAccessible() {
        for (Field f :
                dataFields) {
            f.setAccessible(true);
        }
        outputField.setAccessible(true);
    }

    /**
     * Starting point for the recursive building of the binary categorization Tree
     *
     * @param minBucketSize
     * @return the built regression tree
     * @see BinaryTree
     */
    public BinaryTree buildTree(int minBucketSize) {
        this.minBucketSize = minBucketSize;
        strategy.initStrategy(minBucketSize, outputField, dataSet);
        populateTree(dataSet, tree.getRoot());
        return tree;
    }

    private void populateTree(List<T> data, Node currentNode) {
        SplitData<T> splitData = findBestSplit(data);

        if (splitData == null || splitData.getFieldToSplitOn() == null ||
                strategy.getMetric(data) == 0) {
            LeafData leafData = strategy.getLeafData(data);
            currentNode.setData(leafData);
            return;
        } else {
            currentNode.setData(splitData);
        }

        Comparable<?> splitValue = splitData.getValueToSplitOn();
        Field dataColumn = splitData.getFieldToSplitOn();

        List<T> leftBranch = getDataSmallerThanSplitValue(data, splitValue, dataColumn);

        // Wenn zu wenig Daten vorhanden sind -> Leafnode
        // Wenn MSE == 0 -> Daten perfekt kategorisiert also Leafnode
        if (leftBranch.size() <= minBucketSize * 2 - 1 ||
                strategy.getMetric(leftBranch) == 0) {
            LeafData leafData = strategy.getLeafData(leftBranch);
            currentNode.setLeft(new Node(leafData));
        } else {
            currentNode.setLeft(new Node());
            populateTree(leftBranch, currentNode.getLeft());
        }

        List<T> rightBranch = getDataGreaterEqualsSplitValue(data, splitValue, dataColumn);

        if (rightBranch.size() <= minBucketSize * 2 - 1 ||
                strategy.getMetric(rightBranch) == 0) {
            LeafData leafData = strategy.getLeafData(rightBranch);
            currentNode.setRight(new Node(leafData));
        } else {
            currentNode.setRight(new Node());
            populateTree(rightBranch, currentNode.getRight());
        }
    }

    private SplitData<T> findBestSplit(List<T> data) {
        List<SplitData<T>> splitList = Collections.synchronizedList(new ArrayList<>());

        ExecutorService es = Executors.newCachedThreadPool();

        for (Field dataColumn :
                dataFields) {
            for (T possibleSplitPoint :
                    data) {
                es.execute(strategy.findSplit(dataColumn, possibleSplitPoint, data, splitList));
            }
        }

        es.shutdown();
        boolean finished = false;
        while (!finished) {
            try {
                finished = es.awaitTermination(1, TimeUnit.MINUTES);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        SplitData<T> splitData;

        if (splitList.size() > 0)
            splitData = Collections.min(splitList, Comparator.comparing(SplitData::getMseOfSplit));
        else return null;

        if (splitData.getFieldToSplitOn() != null)
            logger.trace("Best Split Point is " + splitData.getFieldToSplitOn().toString() +
                    " at Value " + splitData.getValueToSplitOn() +
                    " with Gini: " + splitData.getMseOfSplit());
        return splitData;
    }

    // ============================
    // Helper Methods
    // ============================

    /**
     * Returns the Field of the class, which is marked with the @{@link es.horm.cart.lib.annotation.OutputField} annotation
     *
     * @param clazz  The class in which the output Field is to be found
     * @return The Field marked with the the @{@link es.horm.cart.lib.annotation.OutputField} annotation
     * @throws RuntimeException if no Field in the given Class is marked with the @{@link es.horm.cart.lib.annotation.OutputField} annotation
     * @see OutputField
     */
    public Field getOutputField(Class<?> clazz) {
        // TODO: Was ist wenn mehrere als Output field deklariert sind
        // TODO: duplicate Code; Same Code exists in Regression.java
        Field[] fieldList = clazz.getDeclaredFields();
        for (Field f :
                fieldList) {
            if (f.getAnnotation(OutputField.class) != null)
                return f;
        }
        throw new RuntimeException("One of the Fields must be marked as OutputField with the OutputField annotation");
    }

    /**
     * Returns all Fields <b>without</b> the @{@link es.horm.cart.lib.annotation.OutputField} annotation
     *
     * @param clazz  The class in which the data Field is to be found
     * @return a list of all datafields
     */
    public static List<Field> getDataFields(Class<?> clazz) {
        //TODO: Was wenn es auch noch andere als Data und OutputField gibt? -> Evtl. zweite Annotation?
        // TODO: duplicate Code; Same Code exists in Regression.java
        Field[] fieldList = clazz.getDeclaredFields();
        return Arrays.stream(fieldList)
                .filter(field -> field.getAnnotation(OutputField.class) == null)
                .collect(Collectors.toUnmodifiableList());
    }

    public Strategy<T> getStrategy() {
        return strategy;
    }

    public void setStrategy(Strategy<T> strategy) {
        this.strategy = strategy;
    }
}
