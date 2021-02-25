package es.horm.cart.example.customstrategy;

import es.horm.cart.lib.data.LeafData;

import java.util.Collections;
import java.util.List;

public class BoxPlotLeaf implements LeafData {

    private final double median;
    private final double lowerQuartile;
    private final double upperQuartile;
    private final double upperWhiskerEnd;
    private final double lowerWhiskerEnd;
    private final List<Double> runaways;

    public BoxPlotLeaf(double median, double lowerQuartile, double upperQuartile, double upperWhiskerEnd, double lowerWhiskerEnd, List<Double> runaways) {
        this.median = median;
        this.lowerQuartile = lowerQuartile;
        this.upperQuartile = upperQuartile;
        this.upperWhiskerEnd = upperWhiskerEnd;
        this.lowerWhiskerEnd = lowerWhiskerEnd;
        this.runaways = Collections.unmodifiableList(runaways);
    }

    public double getMedian() {
        return median;
    }

    public double getLowerQuartile() {
        return lowerQuartile;
    }

    public double getUpperQuartile() {
        return upperQuartile;
    }

    public double getUpperWhiskerEnd() {
        return upperWhiskerEnd;
    }

    public double getLowerWhiskerEnd() {
        return lowerWhiskerEnd;
    }

    public List<Double> getRunaways() {
        return runaways;
    }

    @Override
    public String toString() {
        return "BoxPlotLeaf{" +
                "median=" + median +
                ", lowerQuartile=" + lowerQuartile +
                ", upperQuartile=" + upperQuartile +
                ", upperWhiskerEnd=" + upperWhiskerEnd +
                ", lowerWhiskerEnd=" + lowerWhiskerEnd +
                ", runaways=" + runaways +
                '}';
    }
}
