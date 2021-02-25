package es.horm.cart.lib.data;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Class to hold the categorization results, which will be stored in the leaves of the decision tree.
 * Stores the possible categorization as a hashmap of class to the corresponding probabilities, so users can themselves
 * decide, what to do with it.
 * Example: If the leaf contains 9 Elements of 3 classes, with 3 elements each, then this class will hold a hashmap with
 * class A -> 0.333333, class B -> 0.333333 and class C -> 0.333333.
 * This way the users can decide on their own, when something counts as classified as class X. (E.g. is it classified as
 * class X when at least 50% of all elements in the leaf are of this classification or 75%?)
 * @see es.horm.cart.lib.data.LeafData
 */
public class LeafDataCategorization implements LeafData {

    private HashMap<Comparable<?>, Double> probabilityMap;

    /**
     * Initializes an empty hashmap
     */
    public LeafDataCategorization() {
        probabilityMap = new HashMap<>();
    }

    /**
     * Adds a probability for a certain category
     * @param category which will be added
     * @param probability probability of this class in the leaf
     */
    public void addProbability(Comparable<?> category, double probability) {
        probabilityMap.put(category, probability);
    }

    /**
     * Used to get the probability of a certain category in this leaf
     * @param category of which the probability will be returned
     * @return the probability of the given category in this leaf
     */
    public double getProbability(Comparable<?> category) {
        return probabilityMap.get(category);
    }

    /**
     * @return the complete probability map, which contains the probabilites for all categories as an <b>unmodifiable Map</b>.
     */
    public Map<Comparable<?>, Double> getProbabilityMap() {
        return Collections.unmodifiableMap(probabilityMap);
    }

    @Override
    public String toString() {
        return probabilityMap.toString();
    }
}
