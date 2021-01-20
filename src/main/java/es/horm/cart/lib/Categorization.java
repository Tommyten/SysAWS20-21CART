package es.horm.cart.lib;

import es.horm.cart.lib.data.LeafData;
import es.horm.cart.lib.data.SplitData;
import es.horm.cart.lib.data.annotation.OutputField;
import es.horm.cart.lib.metrics.Gini;
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

public class Categorization<T> {

    private static final Logger logger = LogManager.getLogger(Regression.class);

    private final Field outputField;
    private final List<Field> dataFields;
    private final List<T> dataSet;

    private final List<?> categories;

    private final BinaryTree tree;

    public Categorization(List<T> dataSet) {
        this.dataSet = dataSet;
        outputField = getOutputField(dataSet.get(0).getClass());
        dataFields = getDataFields(dataSet.get(0).getClass());
        setAllFieldsAccessible();

        categories = getAllCategoriesDistinct(dataSet);
        for (Object obj :
                categories) {
            System.out.println(obj);
        }

        tree = new BinaryTree();
        tree.setRoot(new Node());
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
     * @return the built regression tree
     * @see BinaryTree
     */
    public BinaryTree buildCategorizationTree() {
        populateTree(dataSet, tree.getRoot());
        System.out.println(tree.toString());
        return tree;
    }

    private void populateTree(List<T> data, Node currentNode) {
        SplitData<T> splitData = findBestSplit(data);

        if(splitData == null || splitData.getFieldToSplitOn() == null) {
            LeafData leafData = new LeafData(getAllCategories(data));
            currentNode.setData(leafData);
            return;
        } else {
            currentNode.setData(splitData);
        }

        Comparable splitValue = splitData.getValueToSplitOn();
        Field dataColumn = splitData.getFieldToSplitOn();

        List<T> leftBranch = getLeftBranch(data, splitValue, dataColumn);

        // Wenn zu wenig Daten vorhanden sind -> Leafnode
        // Wenn MSE == 0 -> Daten perfekt kategorisiert also Leafnode
        if (leftBranch.size() <= Parameters.MIN_BUCKET_SIZE * 2 - 1 ||
                Gini.calculateGiniForGroup(leftBranch, outputField, categories) == 0) {
            LeafData leafData = new LeafData(getAllCategories(leftBranch));
            currentNode.setLeft(new Node(leafData));
        } else {
            currentNode.setLeft(new Node());
            populateTree(leftBranch, currentNode.getLeft());
        }

        List<T> rightBranch = getRightBranch(data, splitValue, dataColumn);

        if (rightBranch.size() <= Parameters.MIN_BUCKET_SIZE * 2 - 1 ||
                Gini.calculateGiniForGroup(leftBranch, outputField, categories) == 0) {
            LeafData leafData = new LeafData(getAllCategories(rightBranch));
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
                es.execute(() -> {
                    Comparable splitValue = getFieldValueAsComparable(possibleSplitPoint, dataColumn);
                    List<T> leftBranchCandidate = getLeftBranch(data, splitValue, dataColumn);
                    if(leftBranchCandidate.size() < Parameters.MIN_BUCKET_SIZE) return;

                    List<T> rightBranchCandidate = getRightBranch(data, splitValue, dataColumn);
                    if(rightBranchCandidate.size() <= Parameters.MIN_BUCKET_SIZE) return;

                    double gini = Gini.calculateGiniForDataset(leftBranchCandidate, rightBranchCandidate, outputField, categories);
                    SplitData<T> splitData = new SplitData<>(gini, dataColumn, possibleSplitPoint, getFieldValueAsComparable(possibleSplitPoint, dataColumn));
                    splitList.add(splitData);
                });
            }
        }

        es.shutdown();
        boolean finished = false;
        while(!finished) {
            try {
                finished = es.awaitTermination(1, TimeUnit.MINUTES);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        SplitData<T> splitData;

        if(splitList.size() > 0)
            splitData =  Collections.min(splitList, Comparator.comparing(SplitData::getMseOfSplit));
        else return null;

        if(splitData.getFieldToSplitOn() != null)
            logger.debug("Best Split Point is " + splitData.getFieldToSplitOn().toString() +
                    " at Value " + splitData.getValueToSplitOn() +
                    " with Gini: " + splitData.getMseOfSplit());
        return splitData;
    }

    // ============================
    // Helper Methods
    // ============================

    /**
     *
     * @param data
     * @param splitValue
     * @param columnToSplitOn
     * @return
     */
    private List<T> getLeftBranch(List<T> data, Comparable splitValue, Field columnToSplitOn) {
        return data.stream()
                .filter(dataPoint -> getFieldValueAsComparable(dataPoint, columnToSplitOn).compareTo(splitValue) < 0)
                .collect(Collectors.toList());
    }

    private List<T> getRightBranch(List<T> data, Comparable splitValue, Field columnToSplitOn) {
        return data.stream()
                .filter(dataPoint -> getFieldValueAsComparable(dataPoint, columnToSplitOn).compareTo(splitValue) >= 0)
                .collect(Collectors.toList());
    }

    /**
     * Returns the Field of the class, which is marked with the @{@link es.horm.cart.lib.data.annotation.OutputField} annotation
     * @see OutputField
     * @param clazz The class in which the output Field is to be found
     * @return The Field marked with the the @{@link es.horm.cart.lib.data.annotation.OutputField} annotation
     * @throws RuntimeException if no Field in the given Class is marked with the @{@link es.horm.cart.lib.data.annotation.OutputField} annotation
     */
    private Field getOutputField(Class clazz) {
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
     * Returns all Fields <b>without</b> the @{@link es.horm.cart.lib.data.annotation.OutputField} annotation
     * @param clazz The class in which the data Field is to be found
     * @return a list of all datafields
     */
    private List<Field> getDataFields(Class clazz) {
        //TODO: Was wenn es auch noch andere als Data und OutputField gibt? -> Evtl. zweite Annotation?
        // TODO: duplicate Code; Same Code exists in Regression.java
        Field[] fieldList = clazz.getDeclaredFields();
        return Arrays.stream(fieldList)
                .filter(field -> field.getAnnotation(OutputField.class) == null)
                .collect(Collectors.toUnmodifiableList());
    }

    private List<?> getAllCategoriesDistinct(List<T> data) {
        return data.stream().map(t -> getFieldValue(t, outputField)).distinct().collect(Collectors.toList());
    }

    private List<?> getAllCategories(List<T> data) {
        return data.stream().map(t -> getFieldValue(t, outputField)).collect(Collectors.toList());
    }
}
