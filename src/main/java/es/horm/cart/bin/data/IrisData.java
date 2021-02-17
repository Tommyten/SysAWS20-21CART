package es.horm.cart.bin.data;

import es.horm.cart.lib.annotation.OutputField;

public class IrisData {

    float sepalLength;
    float sepalWidth;
    float petalLength;
    float petalWidth;
    @OutputField
    IrisType type;

    public IrisData() {
    }

    public IrisData(float sepalLength, float sepalWidth, float petalLength, float petalWidth, IrisType type) {
        this.sepalLength = sepalLength;
        this.sepalWidth = sepalWidth;
        this.petalLength = petalLength;
        this.petalWidth = petalWidth;
        this.type = type;
    }

    public float getSepalLength() {
        return sepalLength;
    }

    public void setSepalLength(float sepalLength) {
        this.sepalLength = sepalLength;
    }

    public float getSepalWidth() {
        return sepalWidth;
    }

    public void setSepalWidth(float sepalWidth) {
        this.sepalWidth = sepalWidth;
    }

    public float getPetalLength() {
        return petalLength;
    }

    public void setPetalLength(float petalLength) {
        this.petalLength = petalLength;
    }

    public float getPetalWidth() {
        return petalWidth;
    }

    public void setPetalWidth(float petalWidth) {
        this.petalWidth = petalWidth;
    }

    public IrisType getType() {
        return type;
    }

    public void setType(IrisType type) {
        this.type = type;
    }
}
