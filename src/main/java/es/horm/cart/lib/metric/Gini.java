package es.horm.cart.lib.metric;

import es.horm.cart.lib.Util;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
With the help of: http://www.learnbymarketing.com/481/decision-tree-flavors-gini-info-gain/
 */

/**
 * Class, which provides Utility Methods for calculating the Gini-Index of a dataset and the weighted Gini-Index of a possible
 * Split.
 */
public final class Gini {

    private Gini() {
        throw new AssertionError("Can't even instantiate this class using reflection");
    }

    /**
     * Calculates the weighted Gini-Index of a possible Split in a dataset.
     * @param groupA One dataset of the possible Split
     * @param groupB The second dataset of the possible Split
     * @param outputField The Field, which is the ouputField and contains the result
     * @param categories A list of all categories, which are possible classifications
     * @param <T> The Type of the Data-class of the training-data
     * @param <U> The Type of the outputField
     * @return The weighted Gini-index for the two given datasets
     */
    public static <T, U> double calculateWeightedGini(List<T> groupA, List<T> groupB, Field outputField, List<U> categories) {
        int dataSetSize = groupA.size()+groupB.size();

        double giniGroupA = calculateGini(groupA, outputField, categories);
        double giniGroupB = calculateGini(groupB, outputField, categories);

        return (double) groupA.size() / dataSetSize * giniGroupA  +  (double) groupB.size() / dataSetSize * giniGroupB;
    }

    /**
     * Calculates the Gini-Index of a dataset
     * @param dataset The dataset of which the gini-index is to be calculated
     * @param outputField The Field, which is the ouputField and contains the result
     * @param categories A list of all categories, which are possible classifications
     * @param <T> The Type of the Data-class of the training-data
     * @param <U> The Type of the outputField
     * @return The gini-index for the dataset
     */
    public static <T, U> double calculateGini(List<T> dataset, Field outputField, List<U> categories) {
        int groupSize = dataset.size();

        if(dataset.size() == 0) return 0;

        //Zähle wie oft, welche Kategorien vorkommen
        HashMap<U, Long> categoryCountMap = new HashMap<>();
        for (U obj :
                categories) {
            long categoryCount = dataset.stream().filter(object -> Util.getFieldValue(object, outputField).equals(obj)).count();
            categoryCountMap.put(obj, categoryCount);
        }

        // berechne Gini
        double gini = 0;
        for (Map.Entry<U, Long> entry :
                categoryCountMap.entrySet()) {
            Long count = entry.getValue();
            gini += Math.pow((double) count/groupSize, 2);
        }
        return 1 - gini;
    }
}
