package es.horm.cart.lib;

import es.horm.cart.lib.data.LeafData;
import es.horm.cart.lib.data.SplitData;
import es.horm.cart.lib.strategy.CategorizationSplitStrategy;
import es.horm.cart.lib.strategy.RegressionSplitStrategy;
import es.horm.cart.lib.strategy.SplitStrategy;
import es.horm.cart.lib.tree.BinaryTree;
import es.horm.cart.lib.tree.Node;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static es.horm.cart.lib.Util.*;

/**
 * Class to generate a Decision Tree using a custom implementation of a CART Algorithm
 *
 * CART stands for Classification And Regression Trees. This CART Algorithm can be used to generate Decision Trees for
 * Classification and Regression Trees. The Algorithm will try to correctly guess, whether the data-POJO belongs to a
 * Regression or a Decision Problem. If it wrongly guesses, you can force it to take the correct strategy by customly
 * setting it.
 *
 * @param <T> The data-POJO, which represents the trainingdata and the data, which is to be classified when the tree is built
 */
public class CART<T> {

    private static final Logger logger = LogManager.getLogger(CART.class);

    private SplitStrategy<T> splitStrategy;

    private final Field outputField;
    private final List<Field> dataFields;
    private final List<T> dataSet;

    private final BinaryTree tree;

    private int minBucketSize;

    /**
     * General Purpose Constructor
     * @param dataSet the dataset on which the decision tree is to be trained
     */
    public CART(List<T> dataSet, Class<T> type) {
        if(dataSet == null || dataSet.isEmpty())
            throw new IllegalArgumentException("Cannot build a decision Tree using an empty dataset!");

        this.dataSet = dataSet;
        outputField = getOutputField(type);
        dataFields = getDataFields(type);

        tree = new BinaryTree();
        tree.setRoot(new Node());
    }

    /**
     * Constructor to set which fields can be used by the cart algorithm to make split decisions
     * @param dataSet the dataset on which the tree is to be trained
     * @param allowedFields the fields which can be used by the cart algorithm
     */
    public CART(List<T> dataSet, List<Field> allowedFields, Class<T> type) {
        this.dataSet = dataSet;
        outputField = getOutputField(type);
        dataFields = allowedFields;

        tree = new BinaryTree();
        tree.setRoot(new Node());
    }

    private void findStrategyIfNotSet() {
        // return, as the user has already set a strategy, which is to be used.
        if(splitStrategy != null) return;
        // Sets the Strategy to be used
        if (outputField.getType().equals(double.class) ||
                outputField.getType().equals(Double.class) ||
                outputField.getType().equals(Float.class) ||
                outputField.getType().equals(float.class)) {
            // If the output field is of Type Double or Float, assume a regression Problem
            logger.info("Assuming a Regression Problem");
            splitStrategy = new RegressionSplitStrategy<>(minBucketSize, outputField);
        } else {
            logger.info("Assuming a Categorization Problem");
            splitStrategy = new CategorizationSplitStrategy<>(minBucketSize, outputField, dataSet);
        }
    }

    /**
     * Starting point for the recursive building of the binary categorization Tree
     *
     * @param minBucketSize the minimum size for a dataset to be split up
     */
    public void buildTree(int minBucketSize) {
        this.minBucketSize = minBucketSize;
        findStrategyIfNotSet();
        populateTree(dataSet, tree.getRoot());
    }

    private void populateTree(List<T> data, Node currentNode) {
        if(data.size() <= minBucketSize || splitStrategy.getMetric(data) == 0) {
            // dataset is to small to be split or dataset is already perfectly pure -> leaf
            LeafData leafData = splitStrategy.getLeafData(data);
            currentNode.setData(leafData);
            return;
        }

        // get best split
        SplitData<T> splitData = findBestSplit(data);

        // Set data of current node in tree
        if (splitData == null || splitData.getFieldToSplitOn() == null) {
            // No split found -> currentNode is leaf
            LeafData leafData = splitStrategy.getLeafData(data);
            currentNode.setData(leafData);
            return;
        } else {
            // Split found -> set Data of current Node to SplitData
            currentNode.setData(splitData);
        }

        // get left branch
        Comparable<?> splitValue = splitData.getValueToSplitOn();
        Field dataColumn = splitData.getFieldToSplitOn();

        List<T> leftBranch = getDataSmallerThanSplitValue(data, splitValue, dataColumn);

        // recursive step for left branch
        currentNode.setLeft(new Node());
        populateTree(leftBranch, currentNode.getLeft());

        // get right branch
        List<T> rightBranch = getDataGreaterEqualsSplitValue(data, splitValue, dataColumn);
        // recursive step for right branch
        currentNode.setRight(new Node());
        populateTree(rightBranch, currentNode.getRight());
    }

    private SplitData<T> findBestSplit(List<T> data) {
        List<SplitData<T>> splitList = splitStrategy.calculateAllSplitsForDataSet(data);
        SplitData<T> splitData;

        if (splitList.isEmpty())
            return null;

        splitData = Collections.min(splitList, Comparator.comparing(SplitData::getMetricOfSplit));

        if (splitData.getFieldToSplitOn() != null)
            logger.trace("Best Split Point is " + splitData.getFieldToSplitOn().toString() +
                    " at Value " + splitData.getValueToSplitOn() +
                    " with Gini: " + splitData.getMetricOfSplit());
        return splitData;
    }

    /**
     * Categorizes an Object by making all decisions in the nodes, returns LeafData when a LeafNode is hit
     * @param toBeCategorized the Object, which is to be categorized
     * @return the LeafData object which represents the objects categorization
     */
    public LeafData categorize(T toBeCategorized) {
        Node currentNode = tree.getRoot();
        while(true) {
            if(currentNode.getData() instanceof LeafData) {
                return (LeafData) currentNode.getData();
            } else {
                SplitData<T> split = (SplitData<T>) currentNode.getData();
                Field splitField = split.getFieldToSplitOn();
                if(getFieldValueAsComparable(toBeCategorized, splitField).compareTo(split.getValueToSplitOn()) < 0)
                    currentNode = currentNode.getLeft();
                else
                    currentNode = currentNode.getRight();
            }
        }
    }

    /**
     * Returns the currently set Split Strategy. Split Strategy is first set automatically when buildTree is called
     * @return the currently set Split Strategy
     */
    public SplitStrategy<T> getSplitStrategy() {
        return splitStrategy;
    }

    /**
     * Sets the Splitstrategy, so users can set their own SplitStrategy. If Split Strategy is set before buildTree is called
     * the auto-recognization of which strategy is to be used is turned off
     * @param splitStrategy the user supplied split strategy
     */
    public void setSplitStrategy(SplitStrategy<T> splitStrategy) {
        this.splitStrategy = splitStrategy;
    }

    /**
     * Gives the binary Tree representation of the resulting Decision Tree
     * @return the binary tree
     */
    public BinaryTree getTree() {
        return tree;
    }

    @Override
    public String toString() {
        return tree.toString();
    }
}
