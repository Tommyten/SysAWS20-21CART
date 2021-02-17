package es.horm.cart.bin.data;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum IrisType {
    @JsonProperty("Iris-setosa")
    IRIS_SETOSA,
    @JsonProperty("Iris-versicolor")
    IRIS_VERSICOLOR,
    @JsonProperty("Iris-virginica")
    IRIS_VIRGINICA,
}
