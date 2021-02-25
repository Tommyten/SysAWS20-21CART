package es.horm.cart.lib;

import es.horm.cart.lib.data.LeafData;
import es.horm.cart.lib.strategy.SplitStrategy;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static es.horm.cart.lib.Util.*;

public class RandomForest<T> {

    private final List<CART<T>> treeList;
    private final List<T> originalDataset;
    private List<List<T>> datasetsForTrees;
    private final Random random = new Random(0);
    private final int numOfTrees;
    private final Class<T> type;
    private SplitStrategy<T> splitStrategy;

    public RandomForest(List<T> dataset, int numOfTrees, Class<T> type) {
        originalDataset = dataset;
        this.numOfTrees = numOfTrees;
        this.type = type;

        treeList = new ArrayList<>(numOfTrees);
    }

    /**
     * Builds a random forest of CARTs.
     */
    public void buildForest(int minBucketSize) {
        buildDatasets();
        buildTrees(minBucketSize);
    }

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
                List<Field> fieldList = new ArrayList<>(getDataFields(originalDataset.get(0).getClass()));
                Collections.shuffle(fieldList, random);
                List<Field> fieldsToUse = fieldList.subList(0, random.nextInt(fieldList.size()));
                CART<T> cart = new CART<>(dataset, fieldsToUse, type);
                if(splitStrategy != null) cart.setSplitStrategy(splitStrategy);
                cart.buildTree(minBucketSize);
                treeList.add(cart);
            });
        }

        es.shutdown();
        boolean finished = false;
        while (!finished) {
            try {
                finished = es.awaitTermination(30, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public List<LeafData> categorize(T data) {
        List<LeafData> leafDataList = new ArrayList<>(treeList.size());
        for (CART<T> cart :
                treeList) {
            leafDataList.add(cart.categorize(data));
        }
        return leafDataList;
    }

    public SplitStrategy<T> getSplitStrategy() {
        return splitStrategy;
    }

    public void setSplitStrategy(SplitStrategy<T> splitStrategy) {
        this.splitStrategy = splitStrategy;
    }
}
