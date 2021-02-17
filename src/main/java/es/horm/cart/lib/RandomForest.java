package es.horm.cart.lib;

import es.horm.cart.lib.data.LeafData;
import es.horm.cart.lib.data.LeafDataCategorization;
import es.horm.cart.lib.data.SplitData;
import es.horm.cart.lib.tree.BinaryTree;
import es.horm.cart.lib.tree.BinaryTreePrinter;
import es.horm.cart.lib.tree.Node;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class RandomForest<T> {

    private final List<BinaryTree> treeList;
    private final List<T> originalDataset;
//    private final List<T> datasetWorkingCopy;
    private List<List<T>> datasetsForTrees;
    private final Random random = new Random(0);
    private final int numOfTrees;
//    private final int minSizeOfDatasets;
//
//    private int numOfEntriesForEachTree;

    public RandomForest(List<T> dataset, int numOfTrees, int minSizeOfDatasets) {
        originalDataset = dataset;
        this.numOfTrees = numOfTrees;
//        this.minSizeOfDatasets = minSizeOfDatasets;
//        datasetWorkingCopy = new ArrayList<>(originalDataset);

        treeList = new ArrayList<>(numOfTrees);
    }

    public RandomForest(List<T> dataset, int numOfTrees) {
        originalDataset = dataset;
        this.numOfTrees = numOfTrees;

        treeList = new ArrayList<>(numOfTrees);
    }

    /*
    Attention: the datasetWorkingCopy can get quite large if:
    - lots of trees
    - minSizeOfDatasets is high

    Arraylist offers best performance for lists.
    Maybe using a list of lists could offer better performance (not tested)
     */

    // Assemble a Dataset, so that there is enough data for each Tree in the
    /*private void buildDatasets(boolean shuffle) {
        // get the number of Entries, that can be used for each tree if the
        // original Dataset were to be used
        numOfEntriesForEachTree = (int) Math.ceil((float) originalDataset.size()/numOfTrees);

        // If the calculated number of entries for each tree is smaller than the configured minimum number of entries
        // for each tree, then append the original dataset as often as necessary
        if(minSizeOfDatasets > numOfEntriesForEachTree) {
            int multi = (int) Math.floor((float)minSizeOfDatasets/ numOfEntriesForEachTree);
            for (int i = 0; i < multi; i++) {
                datasetWorkingCopy.addAll(originalDataset);
            }
        }
        // recalculate the number of entries that can be used for each tree (the last tree might have less elements)
        numOfEntriesForEachTree = (int) Math.ceil((float)datasetWorkingCopy.size()/numOfTrees);

        // Shuffle the assembled dataset if flag is set
        if(shuffle)
            Collections.shuffle(datasetWorkingCopy, random);
    }*/

    private void buildDatasets() {
        // Sample with replacement
        datasetsForTrees = Collections.synchronizedList(new ArrayList<>(numOfTrees));

        ExecutorService es = Executors.newCachedThreadPool();

        for (int i = 0; i < numOfTrees; i++) {
            es.execute(() -> {
                List<T> dataset = new ArrayList<>();
                for (int j = 0; j < originalDataset.size(); j++) {
                    dataset.add(originalDataset.get(random.nextInt(originalDataset.size())));
                }
                datasetsForTrees.add(dataset);
            });
        }

        es.shutdown();
        boolean finished = false;
        while (!finished) {
            try {
                finished = es.awaitTermination(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }



    private void buildTrees(int minBucketSize) {
        ExecutorService es = Executors.newFixedThreadPool(10);


        for (List<T> dataset :
                datasetsForTrees) {
            es.execute(() -> {
                List<Field> fieldList = new ArrayList<>(CART.getDataFields(originalDataset.get(0).getClass()));
                Collections.shuffle(fieldList, random);
                CART<T> cart = new CART<>(dataset, fieldList.subList(0, random.nextInt(fieldList.size())));
                treeList.add(cart.buildTree(10));
            });
        }

        es.shutdown();
        boolean finished = false;
        while (!finished) {
            try {
                finished = es.awaitTermination(minBucketSize, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Builds a random forest of CARTs.
     *
     */
    public void buildForest(int minBucketSize) {
        buildDatasets();
        buildTrees(minBucketSize);
    }

    public HashMap<Comparable<?>, Double> findInForest(T indian) {
        List<LeafData> leafDataList = findInAllTrees(indian);
        HashMap<Comparable<?>, Double> probabilities = new HashMap<>();
        for (LeafData data :
                leafDataList) {
            LeafDataCategorization leafData = (LeafDataCategorization) data;
            for (Map.Entry<Comparable<?>, Double> entry :
                    leafData.getProbabilityMap().entrySet()) {
                if (probabilities.containsKey(entry.getKey())) {
                    probabilities.put(entry.getKey(), probabilities.get(entry.getKey()) + entry.getValue());
                } else {
                    probabilities.put(entry.getKey(), entry.getValue());
                }
            }
        }
        probabilities.replaceAll((k, v) -> v / numOfTrees);
        return probabilities;
    }

    public List<LeafData> findInAllTrees(T data) {
        List<LeafData> leafDataList = new ArrayList<>(treeList.size());
        for (BinaryTree tree :
                treeList) {
            leafDataList.add(findValue(tree, data));
        }
        return leafDataList;
    }

    public static <T> LeafData findValue(BinaryTree tree, T data) {
        Node currentNode = tree.getRoot();
        while (true) {
            if (currentNode.getData() instanceof LeafData) {
                return (LeafData) currentNode.getData();

            } else if (currentNode.getData() instanceof SplitData) {
                // Suppress this warning as there is no way (that I know of) to go without it, as you cannot check
                // for generics with instanceof :(
                @SuppressWarnings("unchecked")
                SplitData<T> split = (SplitData<T>) currentNode.getData();
                Field splitField = split.getFieldToSplitOn();
                if (Util.getFieldValueAsComparable(data, splitField).compareTo(split.getValueToSplitOn()) < 0)
                    currentNode = currentNode.getLeft();
                else
                    currentNode = currentNode.getRight();
            }
        }
    }
}
