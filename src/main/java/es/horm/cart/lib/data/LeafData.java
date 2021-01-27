package es.horm.cart.lib.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface LeafData {

    /*private Object output;

    public LeafData(Object output) {
        this.output = output;
    }

    public Object getOutput() {
        return output;
    }

    public void setOutput(Object output) {
        this.output = output;
    }

    @Override
    public String toString() {
        if(output instanceof List<?>) {
            List<?> list = (List<?>) output;
            StringBuilder outputString = new StringBuilder();

            HashMap<Object, Long> countOfOccurences = new HashMap<>();
            for (Object obj :
                    list) {
                long categoryCount = list.stream()
                        .filter(object -> object == obj)
                        .count();
                countOfOccurences.put(obj, categoryCount);
            }

            for (Map.Entry<Object, Long> entry :
                    countOfOccurences.entrySet()) {
                outputString.append(entry.getKey().toString());
                outputString.append(": ");
                outputString.append(String.format("%.2f", ((float)entry.getValue() / list.size()) * 100));
                outputString.append("% ");
            }
            return outputString.toString();
        }
        else
            return "Output Value: " + output.toString();
    }*/
}
