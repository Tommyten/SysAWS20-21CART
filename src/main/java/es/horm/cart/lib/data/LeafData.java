package es.horm.cart.lib.data;

import es.horm.cart.lib.Util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LeafData {

    private double outputValue;
    private List<?> outputCategories;

    public LeafData(double outputValue) {
        this.outputValue = outputValue;
    }

    public LeafData(List<?> outputCategories) {
        this.outputCategories = outputCategories;
    }

    public List<?> getOutputCategories() {
        return outputCategories;
    }

    public double getOutputValue() {
        return outputValue;
    }

    @Override
    public String toString() {
        if (outputCategories == null)
            return "Output Value: " + outputValue;
        else {
            StringBuilder output = new StringBuilder();

            HashMap<Object, Long> countOfOccurences = new HashMap<>();
            for (Object obj :
                    outputCategories) {
                long categoryCount = outputCategories.stream()
                        .filter(object -> object == obj)
                        .count();
                countOfOccurences.put(obj, categoryCount);
            }

            for (Map.Entry<Object, Long> entry :
                    countOfOccurences.entrySet()) {
                output.append(entry.getKey().toString());
                output.append(": ");
                output.append(String.format("%.2f", ((float)entry.getValue() / outputCategories.size()) * 100));
                output.append("% ");
            }
            return output.toString();
        }
    }
}
