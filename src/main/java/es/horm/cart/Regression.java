package es.horm.cart;

import es.horm.cart.data.LeafData;
import es.horm.cart.data.SplitData;
import es.horm.cart.data.annotation.OutputField;
import es.horm.cart.metrics.MeanSquaredError;
import es.horm.cart.tree.BinaryTree;
import es.horm.cart.tree.Node;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static es.horm.cart.Util.getFieldValue;

/**
 *
 * @param <T>
 */
public class Regression<T> {

    private static final Logger logger = LogManager.getLogger(Regression.class);

    private final Field outputField;
    private final List<Field> dataFields;
    private final List<T> dataSet;

    private BinaryTree tree;

    public Regression(List<T> dataSet) {
        this.dataSet = dataSet;
        outputField = getOutputField(dataSet.get(0).getClass());
        dataFields = getDataFields(dataSet.get(0).getClass());

        tree = new BinaryTree();
        tree.setRoot(new Node());

        setAllFieldsAccessible();
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
     * Starting point for the recursive building of the binary regression Tree
     * @return the built regression tree
     * @see BinaryTree
     */
    public BinaryTree buildRegressionTree() {
        populateTree(dataSet, tree.getRoot());
        System.out.println(tree.toString());
        return tree;
    }

    /**
     *
     * @param data
     * @param currentNode
     */
    private void populateTree(List<T> data, Node currentNode) {

        SplitData<T> splitData = findBestSplitParallel(data);

        if(splitData == null || splitData.getFieldToSplitOn() == null) {
            LeafData leafData = new LeafData(getAverageOfOutputField(data));
            currentNode.setData(leafData);
            return;
        } else {
            currentNode.setData(splitData);
        }

        double splitValue = splitData.getValueToSplitOn();
        Field dataColumn = splitData.getFieldToSplitOn();

        List<T> leftBranch = getLeftBranch(data, splitValue, dataColumn);

        // Wenn zu wenig Daten vorhanden sind -> Leafnode
        // Wenn MSE == 0 -> Daten perfekt kategorisiert also Leafnode
        if (leftBranch.size() <= Parameters.MIN_BUCKET_SIZE * 2 - 1 ||
                MeanSquaredError.calculateMeanSquaredError(leftBranch, outputField) == 0) {
            LeafData leafData = new LeafData(getAverageOfOutputField(leftBranch));
            currentNode.setLeft(new Node(leafData));
        } else {
            currentNode.setLeft(new Node());
            populateTree(leftBranch, currentNode.getLeft());
        }

        List<T> rightBranch = getRightBranch(data, splitValue, dataColumn);

        if (rightBranch.size() <= Parameters.MIN_BUCKET_SIZE * 2 - 1 ||
                MeanSquaredError.calculateMeanSquaredError(rightBranch, outputField) == 0) {
            LeafData leafData = new LeafData(getAverageOfOutputField(rightBranch));
            currentNode.setRight(new Node(leafData));
        } else {
            currentNode.setRight(new Node());
            populateTree(rightBranch, currentNode.getRight());
        }
    }

    /**
     * Parallelized Version of findBestSplit!
     * @param dataSet
     * @return
     */
    private SplitData<T> findBestSplitParallel(List<T> dataSet) {
        List<SplitData<T>> splitList = Collections.synchronizedList(new ArrayList<>());

        ExecutorService es = Executors.newCachedThreadPool();

        for (Field dataColumn :
                dataFields) {
            for (T possibleSplitPoint :
                    dataSet) {
                es.execute(() -> {
                    double splitValue = getFieldValue(possibleSplitPoint, dataColumn);
                    List<T> leftBranchCandidate = getLeftBranch(dataSet, splitValue, dataColumn);
                    if(leftBranchCandidate.size() < Parameters.MIN_BUCKET_SIZE) return;

                    List<T> rightBranchCandidate = getRightBranch(dataSet, splitValue, dataColumn);
                    if(rightBranchCandidate.size() <= Parameters.MIN_BUCKET_SIZE) return;

                    double smallerThanCandidateMSE = MeanSquaredError.calculateMeanSquaredError(leftBranchCandidate, outputField);
                    double greaterEqualsCandidateMSE = MeanSquaredError.calculateMeanSquaredError(rightBranchCandidate, outputField);
                    double totalMSE = smallerThanCandidateMSE + greaterEqualsCandidateMSE;

                    SplitData<T> splitData = new SplitData<>(totalMSE, dataColumn, possibleSplitPoint, getFieldValue(possibleSplitPoint, dataColumn));
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
                    " with MSE: " + splitData.getMseOfSplit());
        return splitData;
    }

    /**
     * Non-Parallelized Version see findBestSplitParallel
     * @param dataSet
     * @return
     */
    @Deprecated
    private SplitData<T> findBestSplit(List<T> dataSet) {
        // TODO: Delete?
        double smallestMSE = Double.MAX_VALUE;
        Field fieldToSplitOn = null;
        T dataPointToSplitOn = null;

        for (Field dataColumn :
                dataFields) {
            for (T possibleSplitPoint :
                    dataSet) {
                double splitValue = getFieldValue(possibleSplitPoint, dataColumn);
                List<T> leftBranchCandidate = getLeftBranch(dataSet, splitValue, dataColumn);
                if(leftBranchCandidate.size() < Parameters.MIN_BUCKET_SIZE) continue;

                List<T> rightBranchCandidate = getRightBranch(dataSet, splitValue, dataColumn);
                if(rightBranchCandidate.size() <= Parameters.MIN_BUCKET_SIZE) continue;

                double smallerThanCandidateMSE = MeanSquaredError.calculateMeanSquaredError(leftBranchCandidate, outputField);
                double greaterEqualsCandidateMSE = MeanSquaredError.calculateMeanSquaredError(rightBranchCandidate, outputField);
                double totalMSE = smallerThanCandidateMSE + greaterEqualsCandidateMSE;

                if (totalMSE < smallestMSE) {
                    smallestMSE = totalMSE;
                    fieldToSplitOn = dataColumn;
                    dataPointToSplitOn = possibleSplitPoint;
                }
            }
        }

        if(fieldToSplitOn != null)
        logger.debug("Best Split Point is " + fieldToSplitOn.toString() + " at Value " + getFieldValue(dataPointToSplitOn, fieldToSplitOn) + " with MSE: " + smallestMSE);
        return new SplitData<>(smallestMSE, fieldToSplitOn, dataPointToSplitOn, getFieldValue(dataPointToSplitOn, fieldToSplitOn));
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
    private List<T> getLeftBranch(List<T> data, double splitValue, Field columnToSplitOn) {
        return data.stream()
                .filter(dataPoint -> getFieldValue(dataPoint, columnToSplitOn) < splitValue)
                .collect(Collectors.toList());
    }

    private List<T> getRightBranch(List<T> data, double splitValue, Field columnToSplitOn) {
        return data.stream()
                .filter(dataPoint -> getFieldValue(dataPoint, columnToSplitOn) >= splitValue)
                .collect(Collectors.toList());
    }

    /**
     * Returns the Field of the class, which is marked with the @{@link es.horm.cart.data.annotation.OutputField} annotation
     * @see OutputField
     * @param clazz The class in which the output Field is to be found
     * @return The Field marked with the the @{@link es.horm.cart.data.annotation.OutputField} annotation
     * @throws RuntimeException if no Field in the given Class is marked with the @{@link es.horm.cart.data.annotation.OutputField} annotation
     */
    private Field getOutputField(Class clazz) {
        // TODO: Was ist wenn mehrere als Output field deklariert sind
        Field[] fieldList = clazz.getDeclaredFields();
        for (Field f :
                fieldList) {
            if (f.getAnnotation(OutputField.class) != null)
                return f;
        }
        throw new RuntimeException("One of the Fields must be marked as OutputField with the OutputField annotation");
    }

    /**
     * Returns all Fields <b>without</b> the @{@link es.horm.cart.data.annotation.OutputField} annotation
     * @param clazz The class in which the data Field is to be found
     * @return a list of all datafields
     */
    private List<Field> getDataFields(Class clazz) {
        //TODO: Was wenn es auch noch andere als Data und OutputField gibt? -> Evtl. zweite Annotation?
        Field[] fieldList = clazz.getDeclaredFields();
        return Arrays.stream(fieldList)
                .filter(field -> field.getAnnotation(OutputField.class) == null)
                .collect(Collectors.toUnmodifiableList());
    }


    private <T> double getAverageOfOutputField(List<T> data) {
        if (data.size() == 0) return Double.NaN;

        return data.stream()
                .mapToDouble(obj -> getFieldValue(obj, outputField))
                .average()
                .getAsDouble();
    }
}
