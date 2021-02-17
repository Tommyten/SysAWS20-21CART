package es.horm.cart.lib.metric;

import es.horm.cart.lib.Util;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
With the help of: http://www.learnbymarketing.com/481/decision-tree-flavors-gini-info-gain/
 */
public class Gini {

    private Gini() {
        throw new AssertionError("Can't even instantiate this class using reflection");
    }

    public static <T, U> double calculateGiniForDataset(List<T> groupA, List<T> groupB, Field outputField, List<U> categories) {
        int dataSetSize = groupA.size()+groupB.size();

        double giniGroupA = calculateGiniForGroup(groupA, outputField, categories);
        double giniGroupB = calculateGiniForGroup(groupB, outputField, categories);

        return (double) groupA.size() / dataSetSize * giniGroupA  +  (double) groupB.size() / dataSetSize * giniGroupB;
    }

    public static <T, U> double calculateGiniForGroup(List<T> group, Field outputField, List<U> categories) {
        int groupSize = group.size();

        if(group.size() == 0) return 0;

        //Zähle wie oft, welche Kategorien vorkommen
        HashMap<U, Long> categoryCountMap = new HashMap<>();
        for (U obj :
                categories) {
            long categoryCount = group.stream().filter(object -> Util.getFieldValue(object, outputField).equals(obj)).count();
            categoryCountMap.put(obj, categoryCount);
        }

        double gini = 0;
        for (Map.Entry<U, Long> entry :
                categoryCountMap.entrySet()) {
            Long count = entry.getValue();
            gini += Math.pow((double) count/groupSize, 2);
        }
        return 1 - gini;
    }
}
