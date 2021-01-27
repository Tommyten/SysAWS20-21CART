package es.horm.cart.lib;

import es.horm.cart.lib.data.LeafData;
import es.horm.cart.lib.data.LeafDataCategorization;
import es.horm.cart.lib.data.SplitData;
import es.horm.cart.lib.tree.BinaryTree;
import es.horm.cart.lib.tree.Node;

import java.lang.reflect.Field;
import java.util.*;

public class RandomForest<T> {

    private final List<BinaryTree> treeList;
    private final List<T> originalDataset;
    private final List<T> datasetWorkingCopy;
    private final Random random = new Random(453);
    private final int numOfTrees;
    private final int minSizeOfDatasets;

    private int numOfEntriesForEachTree;

    public RandomForest(List<T> dataset, int numOfTrees, int minSizeOfDatasets) {
        originalDataset = dataset;
        this.numOfTrees = numOfTrees;
        this.minSizeOfDatasets = minSizeOfDatasets;
        datasetWorkingCopy = new ArrayList<>(originalDataset);

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
    private void buildDataset(boolean shuffle) {
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
    }

    private void buildTrees() {
        for (int i = 0; i < numOfTrees; i++) {
            // calculate the index of the last element to be used for the current Tree
            int upperBound = (i+1)*numOfEntriesForEachTree;
            // prevent IndexOutOfBoundsException
            upperBound = Math.min(upperBound, datasetWorkingCopy.size());

            // build the actual tree and add it to the tree list
            CART<T> cart = new CART<>(datasetWorkingCopy.subList(i*numOfTrees, upperBound));
            treeList.add(cart.buildTree(10));
        }
    }

    /**
     * Builds a random forest of CARTs.
     * @param shuffle whether the dataset should be shuffled prior to building the forest
     */
    public void buildForest(boolean shuffle) {
        buildDataset(shuffle);
        buildTrees();
    }

    public HashMap<Comparable<?>, Double> findInForest(T indian) {
        List<LeafData> leafDataList = findInAllTrees(indian);
        HashMap<Comparable<?>, Double> probabilities = new HashMap<>();
        for (LeafData data :
                leafDataList) {
            LeafDataCategorization leafData = (LeafDataCategorization) data;
            for (Map.Entry<Comparable<?>, Double> entry:
                    leafData.getProbabilityMap().entrySet()){
                if(probabilities.containsKey(entry.getKey())) {
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
        while(true) {
            if(currentNode.getData() instanceof LeafData) {
                return (LeafData) currentNode.getData();

            } else if(currentNode.getData() instanceof SplitData){
                // Suppress this warning as there is no way (that I know of) to go without it, as you cannot check
                // for generics with instanceof :(
                @SuppressWarnings("unchecked")
                SplitData<T> split = (SplitData<T>) currentNode.getData();
                Field splitField = split.getFieldToSplitOn();
                if(Util.getFieldValueAsComparable(data, splitField).compareTo(split.getValueToSplitOn()) < 0)
                    currentNode = currentNode.getLeft();
                else
                    currentNode = currentNode.getRight();
            }
        }
    }
}
