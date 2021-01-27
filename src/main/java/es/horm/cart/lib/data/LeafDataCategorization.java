package es.horm.cart.lib.data;

import java.util.HashMap;

public class LeafDataCategorization implements LeafData {

    private HashMap<Comparable<?>, Double> probabilityMap;

    public LeafDataCategorization() {
        probabilityMap = new HashMap<>();
    }

    public void addProbability(Comparable<?> category, double probability) {
        probabilityMap.put(category, probability);
    }

    public double getProbability(Comparable<?> category) {
        return probabilityMap.get(category);
    }

    public HashMap<Comparable<?>, Double> getProbabilityMap() {
        return probabilityMap;
    }

    public void setProbabilityMap(HashMap<Comparable<?>, Double> probabilityMap) {
        this.probabilityMap = probabilityMap;
    }

    @Override
    public String toString() {
        return probabilityMap.toString();
    }
}
